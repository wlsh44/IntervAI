#!/usr/bin/env sh
set -eu

if [ ! -f ".env" ]; then
  echo ".env file not found in $(pwd)" >&2
  exit 1
fi

docker compose pull spring-app
docker compose up -d spring-app
docker image prune -f
docker compose ps
