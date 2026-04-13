#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck disable=SC1091
source "${SCRIPT_DIR}/common.sh"

load_access_token
load_interview_id
OUTPUT_FILE="${TMP_DIR}/05-create-questions.json"

echo "[5/8] create questions"

save_body "${OUTPUT_FILE}" POST "${BASE_URL}/api/interviews/${INTERVIEW_ID}/questions" \
  "Authorization:Bearer ${ACCESS_TOKEN}"

echo "question count: $(jq '.questions | length' "${OUTPUT_FILE}")"
