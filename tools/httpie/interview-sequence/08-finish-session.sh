#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck disable=SC1091
source "${SCRIPT_DIR}/common.sh"

load_access_token
load_interview_id

echo "[8/8] finish session"

http --check-status --ignore-stdin POST "${BASE_URL}/api/interviews/${INTERVIEW_ID}/sessions/finish" \
  "Authorization:Bearer ${ACCESS_TOKEN}"

echo "finish request completed"
