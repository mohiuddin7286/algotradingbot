/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      boxShadow: {
        terminal: "0 20px 60px rgba(15, 23, 42, 0.45)",
      },
      colors: {
        terminal: {
          950: "#020617",
          900: "#0f172a",
          800: "#1e293b",
          700: "#334155",
          400: "#94a3b8",
          200: "#e2e8f0",
        },
      },
    },
  },
  plugins: [],
};