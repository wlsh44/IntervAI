#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
echo ${SCRIPT_DIR}
# shellcheck disable=SC1091
source "${SCRIPT_DIR}/common.sh"

OUTPUT_FILE="${TMP_DIR}/01-sign-up.json"

echo "[1/8] sign-up"

save_body "${OUTPUT_FILE}" POST "${BASE_URL}/api/users/sign-up" \
  nickname="${TEST_NICKNAME}" \
  password="${TEST_PASSWORD}"

jq -r '.accessToken' "${OUTPUT_FILE}" > "${ACCESS_TOKEN_FILE}"

echo "saved access token -> ${ACCESS_TOKEN_FILE}"
