# Algo Trading Bot

## Vercel deployment

The React frontend is Vercel-ready. The FastAPI backend is also exposed as a Vercel Python function through `api/index.py`, so the whole repo can be deployed from one Vercel project.

### Frontend

1. Set the Vercel project root to `frontend` or use the root `vercel.json` in this repo.
2. Set `VITE_API_BASE_URL` in Vercel only if you want to override the default same-origin `/api` path.
3. Run the frontend build with `npm run build` from `frontend`.

### Backend

1. Vercel loads the FastAPI app from `api/index.py`, which imports the existing backend app.
2. The backend dependencies live in the root `requirements.txt` so Vercel can detect them.
3. The trained model file under `backend/bot/` is excluded from function bundling.

### Local development

1. Run the backend on port `8000`.
2. Run the frontend on port `5173`.
3. Create `frontend/.env.local` if you want to override the API URL locally.