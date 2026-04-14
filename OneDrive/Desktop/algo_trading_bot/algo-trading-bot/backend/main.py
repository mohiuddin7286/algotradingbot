from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from api.routes_bot import router as bot_router
from api.routes_data import router as data_router


app = FastAPI(title="Algo Trading Bot API")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(data_router, prefix="/api/data", tags=["data"])
app.include_router(bot_router, prefix="/api/bot", tags=["bot"])


@app.get("/")
def read_root() -> dict[str, str]:
    return {"status": "ok", "service": "algo-trading-bot"}