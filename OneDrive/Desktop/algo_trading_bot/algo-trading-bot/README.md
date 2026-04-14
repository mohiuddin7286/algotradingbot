# Algo Trading Bot

## Vercel deployment

The safest Vercel setup for this repo is two projects:

1. A frontend project with the root set to `frontend`.
2. A backend project with the root set to `backend`.

The monorepo root still contains Vercel config for the combined setup, but if you are seeing a Vercel 404, it usually means the project was imported from the wrong folder or Vercel is serving the wrong root.

### Frontend

1. Set the Vercel project root to `frontend`.
2. Use the frontend-specific [frontend/vercel.json](frontend/vercel.json) for the build and SPA fallback.
3. Set `VITE_API_BASE_URL` in Vercel to the backend URL if the backend is deployed separately.

### Backend

1. Set the Vercel project root to `backend` if you want the FastAPI app deployed separately.
2. The backend app is defined in [backend/main.py](backend/main.py).
3. The trained model file under `backend/bot/` is excluded from function bundling in the root Vercel config.

### Local development

1. Run the backend on port `8000`.
2. Run the frontend on port `5173`.
3. Create `frontend/.env.local` if you want to override the API URL locally.