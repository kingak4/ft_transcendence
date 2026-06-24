#!/usr/bin/env bash
# Copies .env.example files into place on first container creation.
# Idempotent — skips files that already exist.
set -euo pipefail

copy_if_missing() {
  local src="$1" dst="$2"
  if [ ! -f "$dst" ]; then
    cp "$src" "$dst"
    echo "  created $dst"
  else
    echo "  skipped $dst (already exists)"
  fi
}

echo "==> Initialising env files..."
copy_if_missing infra/.env.example             infra/.env
copy_if_missing infra/postgres/.env.example    infra/postgres/.env
copy_if_missing infra/redis/.env.example       infra/redis/.env
copy_if_missing frontend/.env.example          frontend/.env
copy_if_missing frontend/.env.example          frontend/.env.local
copy_if_missing backend/.env.example           backend/.env
copy_if_missing backend/transcend/.env.example backend/transcend/.env

echo ""
echo "==> Ready. Start the stack in this order:"
echo ""
echo "    1. cd infra  && make up"
echo "       cd infra/nginx && docker compose up -d"
echo ""
echo "    2. cd backend  && make -f Makefile.dev up"
echo ""
echo "    3. cd frontend && make -f Makefile.dev up"
echo ""
echo "    Access points:"
echo "      http://localhost:3000   — frontend (direct)"
echo "      https://localhost:8443  — frontend via nginx (HTTPS)"
echo "      http://localhost:5001/swagger-ui.html — backend Swagger UI"
