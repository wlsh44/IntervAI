#!/usr/bin/env sh
set -eu

APP_SERVICE="spring-app"
ENV_FILE=".env"

if [ ! -f "$ENV_FILE" ]; then
  echo "$ENV_FILE file not found in $(pwd)" >&2
  exit 1
fi

echo "[1/4] Pull latest image..."
docker compose --env-file "$ENV_FILE" pull "$APP_SERVICE"

echo "[2/4] Recreate container..."
docker compose --env-file "$ENV_FILE" up -d --force-recreate "$APP_SERVICE"

echo "[3/4] Remove dangling images..."
docker image prune -f

echo "[4/4] Service status..."
docker compose --env-file "$ENV_FILE" ps
