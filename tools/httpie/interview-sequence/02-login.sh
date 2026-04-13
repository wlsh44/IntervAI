#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck disable=SC1091
source "${SCRIPT_DIR}/common.sh"

OUTPUT_FILE="${TMP_DIR}/02-login.json"

echo "[2/8] login"

save_auth_body "${OUTPUT_FILE}" POST "${BASE_URL}/api/users/login" \
  nickname="${TEST_NICKNAME}" \
  password="${TEST_PASSWORD}"

jq -r '.accessToken' "${OUTPUT_FILE}" > "${ACCESS_TOKEN_FILE}"

echo "saved access token -> ${ACCESS_TOKEN_FILE}"
echo "saved session cookie -> ${SESSION_FILE}"
