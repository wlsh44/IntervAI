#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck disable=SC1091
source "${SCRIPT_DIR}/common.sh"

load_access_token
load_interview_id
OUTPUT_FILE="${TMP_DIR}/04-create-session.json"

echo "[4/8] create session"

save_body "${OUTPUT_FILE}" POST "${BASE_URL}/api/interviews/${INTERVIEW_ID}/sessions" \
  "Authorization:Bearer ${ACCESS_TOKEN}"

jq -r '.sessionId' "${OUTPUT_FILE}" > "${SESSION_ID_FILE}"

echo "saved session id -> ${SESSION_ID_FILE}"
