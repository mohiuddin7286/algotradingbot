import axios from 'axios';

const getDefaultBaseUrl = () => {
  if (typeof window !== 'undefined') {
    const isLocalHost = ['localhost', '127.0.0.1'].includes(window.location.hostname);

    if (isLocalHost) {
      return 'http://localhost:8000/api';
    }
  }

  return '/api';
};

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || getDefaultBaseUrl(),
});

export const fetchHistoricalData = async (ticker) => {
  const response = await api.get(`/data/historical/${encodeURIComponent(ticker)}`);
  return response.data;
};

export const fetchBacktestResults = async (ticker) => {
  const response = await api.get(`/bot/backtest/${encodeURIComponent(ticker)}`);
  return response.data;
};

export default api;