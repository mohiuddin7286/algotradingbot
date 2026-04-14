from __future__ import annotations

import sys
from pathlib import Path


BACKEND_DIR = Path(__file__).resolve().parent.parent / "backend"
BACKEND_DIR_STR = str(BACKEND_DIR)

if BACKEND_DIR_STR not in sys.path:
    sys.path.insert(0, BACKEND_DIR_STR)

from main import app  # noqa: E402