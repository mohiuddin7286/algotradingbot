from __future__ import annotations

from pathlib import Path

from fastapi import APIRouter, HTTPException
import pandas as pd
import yfinance as yf

from bot.env import StockTradingEnv


router = APIRouter()
BASE_DIR = Path(__file__).resolve().parent.parent / "bot"
MODEL_PATH = BASE_DIR / "ppo_trading_bot.zip"


def _normalize_price_frame(frame: pd.DataFrame) -> pd.DataFrame:
    normalized = frame.copy()
    if isinstance(normalized.columns, pd.MultiIndex):
        normalized.columns = normalized.columns.get_level_values(0)
    return normalized


def _prepare_backtest_data(ticker: str) -> pd.DataFrame:
    data = yf.download(ticker.upper(), period="3mo", interval="1d", progress=False)
    if data.empty:
        raise HTTPException(status_code=404, detail=f"No backtest data found for {ticker.upper()}")

    data = _normalize_price_frame(data).rename_axis("Date")
    data["SMA_20"] = data["Close"].rolling(window=20, min_periods=1).mean()
    return data[["Open", "High", "Low", "Close", "Volume", "SMA_20"]].tail(60).reset_index()


@router.get("/backtest/{ticker}")
def backtest_bot(ticker: str) -> dict:
    try:
        from stable_baselines3 import PPO
    except ImportError as import_error:
        raise HTTPException(
            status_code=503,
            detail="stable-baselines3 is not installed yet. Install backend requirements before calling backtest.",
        ) from import_error

    if not MODEL_PATH.exists():
        raise HTTPException(
            status_code=500,
            detail=f"Trained model not found at {MODEL_PATH}. Run backend/bot/train.py first.",
        )

    data = _prepare_backtest_data(ticker)
    if data.empty:
        raise HTTPException(status_code=404, detail=f"Not enough data to backtest {ticker.upper()}")

    model = PPO.load(MODEL_PATH)
    env = StockTradingEnv(data[["Open", "High", "Low", "Close", "Volume", "SMA_20"]])

    observation, _ = env.reset()
    starting_balance = float(env.initial_balance)
    trades: list[dict] = []

    done = False
    while not done:
        action, _ = model.predict(observation, deterministic=True)
        current_index = min(env.current_step, len(data) - 1)
        current_row = data.iloc[current_index]
        current_price = float(current_row["Close"])
        date = pd.to_datetime(current_row["Date"]).strftime("%Y-%m-%d")

        if int(action) == 1:
            shares_to_buy = int(env.balance // current_price)
            if shares_to_buy > 0:
                trades.append(
                    {
                        "date": date,
                        "action": "BUY",
                        "price": round(current_price, 2),
                        "shares": shares_to_buy,
                    }
                )
        elif int(action) == 2 and env.shares_held > 0:
            trades.append(
                {
                    "date": date,
                    "action": "SELL",
                    "price": round(current_price, 2),
                    "shares": int(env.shares_held),
                }
            )

        observation, _, terminated, truncated, _ = env.step(int(action))
        done = terminated or truncated

    final_price = float(data.iloc[-1]["Close"])
    final_value = float(env.balance + env.shares_held * final_price)
    total_profit = final_value - starting_balance

    return {
        "ticker": ticker.upper(),
        "starting_balance": starting_balance,
        "final_value": final_value,
        "total_profit": total_profit,
        "trades": trades,
    }