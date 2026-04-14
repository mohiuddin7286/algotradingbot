from __future__ import annotations

from dataclasses import dataclass

import gymnasium as gym
import numpy as np
import pandas as pd
from gymnasium import spaces


@dataclass
class TradingState:
    balance: float
    shares_held: float
    net_worth: float


class StockTradingEnv(gym.Env):
    metadata = {"render_modes": ["human"]}

    def __init__(self, data: pd.DataFrame, initial_balance: float = 10000.0):
        super().__init__()
        self.data = data.copy().reset_index(drop=True)
        if "SMA_20" not in self.data.columns:
            self.data["SMA_20"] = self.data["Close"].rolling(window=20, min_periods=1).mean()
        self.initial_balance = float(initial_balance)
        self.action_space = spaces.Discrete(3)
        self.observation_space = spaces.Box(
            low=0.0,
            high=np.finfo(np.float32).max,
            shape=(4,),
            dtype=np.float32,
        )
        self.current_step = 0
        self.balance = self.initial_balance
        self.shares_held = 0.0
        self.net_worth = self.initial_balance

    def _current_row(self) -> pd.Series:
        index = min(self.current_step, len(self.data) - 1)
        return self.data.iloc[index]

    @staticmethod
    def _scalar(value):
        if isinstance(value, pd.Series):
            return value.iloc[0]
        if isinstance(value, np.ndarray):
            return value.reshape(-1)[0]
        return value

    def _get_observation(self) -> np.ndarray:
        row = self._current_row()
        current_price = float(self._scalar(row["Close"]))
        sma_20 = self._scalar(row.get("SMA_20", current_price))
        if pd.isna(sma_20):
            sma_20 = current_price
        return np.array(
            [self.balance, self.shares_held, current_price, float(sma_20)],
            dtype=np.float32,
        )

    def _execute_action(self, action: int, price: float) -> None:
        if action == 1:
            shares_to_buy = int(self.balance // price)
            if shares_to_buy > 0:
                self.balance -= shares_to_buy * price
                self.shares_held += shares_to_buy
        elif action == 2 and self.shares_held > 0:
            self.balance += self.shares_held * price
            self.shares_held = 0.0

    def reset(self, seed: int | None = None, options: dict | None = None):
        super().reset(seed=seed)
        self.current_step = 0
        self.balance = self.initial_balance
        self.shares_held = 0.0
        self.net_worth = self.initial_balance
        return self._get_observation(), {}

    def step(self, action: int):
        current_row = self._current_row()
        current_price = float(self._scalar(current_row["Close"]))
        previous_net_worth = self.balance + self.shares_held * current_price

        self._execute_action(action, current_price)

        self.current_step += 1
        terminated = self.current_step >= len(self.data)
        truncated = False

        if terminated:
            next_price = current_price
        else:
            next_price = float(self._scalar(self._current_row()["Close"]))

        self.net_worth = self.balance + self.shares_held * next_price
        reward = self.net_worth - previous_net_worth
        observation = self._get_observation()

        return observation, float(reward), terminated, truncated, {"net_worth": float(self.net_worth)}

    def render(self):
        print(
            f"Step: {self.current_step} | Balance: {self.balance:.2f} | Shares: {self.shares_held:.2f} | Net Worth: {self.net_worth:.2f}"
        )