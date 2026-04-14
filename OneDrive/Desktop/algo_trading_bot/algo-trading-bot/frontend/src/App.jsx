import { useState, useEffect } from 'react';
import { fetchHistoricalData, fetchBacktestResults } from './services/api';
import TradingChart from './components/TradingChart';

function App() {
  const [tickerInput, setTickerInput] = useState('AAPL');
  const [activeTicker, setActiveTicker] = useState('AAPL');
  const [stockData, setStockData] = useState([]);
  const [botResults, setBotResults] = useState(null);
  const [loading, setLoading] = useState(false);

  const loadDashboard = async (tickerToLoad) => {
    try {
      setLoading(true);
      const history = await fetchHistoricalData(tickerToLoad);
      setStockData(history.data);
      
      const backtest = await fetchBacktestResults(tickerToLoad);
      setBotResults(backtest);
      setActiveTicker(tickerToLoad.toUpperCase());
    } catch (error) {
      console.error("Error loading data:", error);
      alert("Failed to load data. Make sure the ticker exists and the backend is running.");
    } finally {
      setLoading(false);
    }
  };

  // Load initial data
  useEffect(() => {
    loadDashboard('AAPL');
  }, []);

  const handleRunBot = (e) => {
    e.preventDefault();
    if (tickerInput.trim()) {
      loadDashboard(tickerInput);
    }
  };

  return (
    <div className="flex h-screen bg-slate-950 text-slate-300 font-sans overflow-hidden">
      
      {/* Sidebar */}
      <aside className="w-64 bg-slate-900 border-r border-slate-800 flex flex-col">
        <div className="p-6 border-b border-slate-800">
          <h1 className="text-2xl font-bold bg-gradient-to-r from-blue-400 to-indigo-500 bg-clip-text text-transparent">
            AlgoBot Pro
          </h1>
          <p className="text-xs text-slate-500 mt-1">RL Trading Terminal v1.0</p>
        </div>
        
        <div className="p-6 flex-1">
          <h2 className="text-xs uppercase tracking-wider text-slate-500 font-semibold mb-4">Control Panel</h2>
          <form onSubmit={handleRunBot} className="space-y-4">
            <div>
              <label className="block text-sm mb-1 text-slate-400">Asset Ticker</label>
              <input 
                type="text" 
                value={tickerInput}
                onChange={(e) => setTickerInput(e.target.value.toUpperCase())}
                placeholder="e.g. TSLA, MSFT"
                className="w-full bg-slate-950 border border-slate-700 rounded-md px-4 py-2 text-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-colors"
              />
            </div>
            <button 
              type="submit"
              disabled={loading}
              className="w-full bg-blue-600 hover:bg-blue-500 text-white font-medium py-2 px-4 rounded-md transition-colors disabled:opacity-50"
            >
              {loading ? 'Running...' : 'Run Backtest'}
            </button>
          </form>

          <div className="mt-8">
             <h2 className="text-xs uppercase tracking-wider text-slate-500 font-semibold mb-4">System Status</h2>
             <div className="flex items-center gap-2 text-sm">
               <span className="w-2 h-2 rounded-full bg-green-500 animate-pulse"></span>
               Model: PPO Online
             </div>
             <div className="flex items-center gap-2 text-sm mt-2">
               <span className="w-2 h-2 rounded-full bg-green-500"></span>
               API: Connected
             </div>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 overflow-y-auto p-8">
        
        <header className="mb-8 flex justify-between items-end">
          <div>
            <h2 className="text-3xl font-light text-white">{activeTicker} <span className="text-slate-500 text-xl ml-2">Backtest Results</span></h2>
          </div>
        </header>

        {loading ? (
          <div className="flex justify-center items-center h-64">
             <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
          </div>
        ) : (
          <div className="space-y-6">
            
            {/* Top Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
              <StatCard title="Starting Balance" value={`$${botResults?.starting_balance?.toLocaleString() || 0}`} />
              <StatCard title="Final Value" value={`$${botResults?.final_value?.toLocaleString() || 0}`} />
              
              {/* Profit Card with Dynamic Colors */}
              <div className={`p-6 rounded-xl border backdrop-blur-sm ${botResults?.profit >= 0 ? 'bg-green-900/10 border-green-800/50' : 'bg-red-900/10 border-red-800/50'}`}>
                <h3 className="text-slate-400 text-sm font-medium mb-1">Net Profit</h3>
                <p className={`text-3xl font-bold ${botResults?.profit >= 0 ? 'text-green-400' : 'text-red-400'}`}>
                  {botResults?.profit >= 0 ? '+' : ''}${botResults?.profit?.toLocaleString() || 0}
                </p>
              </div>

              <StatCard title="Total Trades" value={botResults?.trades?.length || 0} />
            </div>

            {/* Chart Area */}
            <div className="bg-slate-900/50 border border-slate-800 rounded-xl p-4 backdrop-blur-sm">
               <h3 className="text-lg font-medium text-white mb-4 px-2">Price Action & AI Executions</h3>
               <TradingChart data={stockData} trades={botResults?.trades} />
            </div>

            {/* Trade History Table */}
            <div className="bg-slate-900/50 border border-slate-800 rounded-xl overflow-hidden backdrop-blur-sm">
               <div className="p-4 border-b border-slate-800">
                  <h3 className="text-lg font-medium text-white">Trade Log</h3>
               </div>
               <div className="overflow-x-auto max-h-64 overflow-y-auto">
                 <table className="w-full text-left text-sm">
                   <thead className="bg-slate-950/50 text-slate-400 sticky top-0">
                     <tr>
                       <th className="px-6 py-3 font-medium">Date</th>
                       <th className="px-6 py-3 font-medium">Action</th>
                       <th className="px-6 py-3 font-medium">Shares</th>
                       <th className="px-6 py-3 font-medium">Price</th>
                       <th className="px-6 py-3 font-medium">Total Value</th>
                     </tr>
                   </thead>
                   <tbody className="divide-y divide-slate-800/50">
                     {botResults?.trades?.slice().reverse().map((trade, i) => (
                       <tr key={i} className="hover:bg-slate-800/30 transition-colors">
                         <td className="px-6 py-4 text-slate-300">{trade.date}</td>
                         <td className="px-6 py-4">
                           <span className={`px-2 py-1 rounded text-xs font-bold ${trade.action === 'BUY' ? 'bg-green-500/20 text-green-400' : 'bg-red-500/20 text-red-400'}`}>
                             {trade.action}
                           </span>
                         </td>
                         <td className="px-6 py-4">{trade.shares}</td>
                         <td className="px-6 py-4">${trade.price.toFixed(2)}</td>
                         <td className="px-6 py-4">${(trade.shares * trade.price).toFixed(2)}</td>
                       </tr>
                     ))}
                     {!botResults?.trades?.length && (
                       <tr>
                         <td colSpan="5" className="px-6 py-8 text-center text-slate-500">No trades executed in this period.</td>
                       </tr>
                     )}
                   </tbody>
                 </table>
               </div>
            </div>

          </div>
        )}
      </main>
    </div>
  );
}

// Reusable micro-component for the top stats
function StatCard({ title, value }) {
  return (
    <div className="bg-slate-900/50 p-6 rounded-xl border border-slate-800 backdrop-blur-sm">
      <h3 className="text-slate-400 text-sm font-medium mb-1">{title}</h3>
      <p className="text-3xl font-bold text-white">{value}</p>
    </div>
  );
}

export default App;