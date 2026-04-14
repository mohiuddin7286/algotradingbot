from fastapi import APIRouter, HTTPException
import pandas as pd
import yfinance as yf


router = APIRouter()


def _normalize_price_frame(frame: pd.DataFrame) -> pd.DataFrame:
    normalized = frame.copy()
    if isinstance(normalized.columns, pd.MultiIndex):
        normalized.columns = normalized.columns.get_level_values(0)
    return normalized


def _format_historical_frame(frame: pd.DataFrame) -> list[dict]:
    cleaned = _normalize_price_frame(frame)
    cleaned = cleaned.reset_index()
    cleaned["Date"] = pd.to_datetime(cleaned["Date"]).dt.strftime("%Y-%m-%d")
    cleaned["SMA_20"] = cleaned["SMA_20"].where(cleaned["SMA_20"].notna(), None)

    records: list[dict] = []
    for _, row in cleaned.iterrows():
        records.append(
            {
                "time": row["Date"],
                "open": float(row["Open"]),
                "high": float(row["High"]),
                "low": float(row["Low"]),
                "close": float(row["Close"]),
                "sma_20": None if pd.isna(row["SMA_20"]) else float(row["SMA_20"]),
                "volume": int(row["Volume"]),
            }
        )
    return records


@router.get("/historical/{ticker}")
def get_historical_data(ticker: str) -> dict:
    history = yf.download(ticker.upper(), period="1y", interval="1d", progress=False)

    if history.empty:
        raise HTTPException(status_code=404, detail=f"No historical data found for {ticker.upper()}")

    history = _normalize_price_frame(history).rename_axis("Date")
    history["SMA_20"] = history["Close"].rolling(window=20, min_periods=1).mean()

    return {
        "ticker": ticker.upper(),
        "data": _format_historical_frame(history[["Open", "High", "Low", "Close", "Volume", "SMA_20"]]),
    }