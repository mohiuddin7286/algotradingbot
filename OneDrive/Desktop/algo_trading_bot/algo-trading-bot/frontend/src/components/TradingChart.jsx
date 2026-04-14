import { useEffect, useRef } from 'react';
import { ColorType, createChart } from 'lightweight-charts';

const TradingChart = ({ historicalData = [], trades = [] }) => {
  const chartContainerRef = useRef(null);
  const chartRef = useRef(null);
  const candleSeriesRef = useRef(null);
  const smaSeriesRef = useRef(null);

  useEffect(() => {
    if (!chartContainerRef.current) {
      return undefined;
    }

    chartRef.current = createChart(chartContainerRef.current, {
        width: chartContainerRef.current.clientWidth,
        height: 400,
        layout: { 
            background: { type: 'solid', color: 'transparent' }, // Made transparent
            textColor: '#94a3b8' // Tailwind slate-400
        },
        grid: { 
            vertLines: { color: 'rgba(51, 65, 85, 0.3)' }, // Very subtle grid
            horzLines: { color: 'rgba(51, 65, 85, 0.3)' } 
        },
        crosshair: {
            mode: 0, // Normal crosshair mode
        },
        timeScale: {
            borderColor: 'rgba(51, 65, 85, 0.5)',
        },
    });

    const candleSeries = chartRef.current.addCandlestickSeries({
      upColor: '#22c55e',
      downColor: '#ef4444',
      borderUpColor: '#22c55e',
      borderDownColor: '#ef4444',
      wickUpColor: '#22c55e',
      wickDownColor: '#ef4444',
    });

    const smaSeries = chartRef.current.addLineSeries({
      color: '#38bdf8',
      lineWidth: 2,
      priceLineVisible: false,
    });

    candleSeriesRef.current = candleSeries;
    smaSeriesRef.current = smaSeries;

    const resizeObserver = new ResizeObserver(() => {
      if (chartContainerRef.current) {
        chartRef.current.applyOptions({ width: chartContainerRef.current.clientWidth });
      }
    });
    resizeObserver.observe(chartContainerRef.current);

    return () => {
      resizeObserver.disconnect();
      chartRef.current.remove();
      chartRef.current = null;
      candleSeriesRef.current = null;
      smaSeriesRef.current = null;
    };
  }, []);

  useEffect(() => {
    if (!candleSeriesRef.current || !smaSeriesRef.current) {
      return;
    }

    const candles = historicalData.map((point) => ({
      time: point.time,
      open: point.open,
      high: point.high,
      low: point.low,
      close: point.close,
    }));

    const smaPoints = historicalData
      .filter((point) => point.sma_20 !== null && point.sma_20 !== undefined)
      .map((point) => ({
        time: point.time,
        value: point.sma_20,
      }));

    candleSeriesRef.current.setData(candles);
    smaSeriesRef.current.setData(smaPoints);
    chartRef.current?.timeScale().fitContent();
  }, [historicalData]);

  useEffect(() => {
    if (!candleSeriesRef.current) {
      return;
    }

    const markers = trades.map((trade) => ({
      time: trade.date,
      position: trade.action === 'BUY' ? 'belowBar' : 'aboveBar',
      color: trade.action === 'BUY' ? '#22c55e' : '#ef4444',
      shape: trade.action === 'BUY' ? 'arrowUp' : 'arrowDown',
      text: `${trade.action} ${trade.shares}`,
    }));

    candleSeriesRef.current.setMarkers(markers);
  }, [trades]);

  return <div ref={chartContainerRef} className="h-[520px] w-full rounded-2xl border border-slate-800 bg-slate-950/70 shadow-terminal" />;
};

export default TradingChart;