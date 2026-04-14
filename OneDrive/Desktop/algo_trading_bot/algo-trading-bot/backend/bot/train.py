from __future__ import annotations

import sys
from pathlib import Path

import pandas as pd
import yfinance as yf
from stable_baselines3 import PPO

if __package__ is None or __package__ == "":
    sys.path.append(str(Path(__file__).resolve().parent.parent))

from bot.env import StockTradingEnv


BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = BASE_DIR / "ppo_trading_bot.zip"


def fetch_training_data() -> pd.DataFrame:
    data = yf.download("AAPL", period="2y", interval="1d", progress=False)
    if data.empty:
        raise RuntimeError("Unable to fetch training data for AAPL")

    if isinstance(data.columns, pd.MultiIndex):
        data.columns = data.columns.get_level_values(0)

    data = data.rename_axis("Date")
    data["SMA_20"] = data["Close"].rolling(window=20, min_periods=1).mean()
    data["SMA_20"] = data["SMA_20"].bfill().fillna(data["Close"].iloc[0])
    return data[["Open", "High", "Low", "Close", "Volume", "SMA_20"]].dropna().reset_index(drop=True)


def train_model(total_timesteps: int = 10_000) -> Path:
    data = fetch_training_data()
    env = StockTradingEnv(data)
    model = PPO("MlpPolicy", env, verbose=1)
    model.learn(total_timesteps=total_timesteps)
    model.save(MODEL_PATH)
    return MODEL_PATH


if __name__ == "__main__":
    saved_model = train_model()
    print(f"Saved model to {saved_model}")