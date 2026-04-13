#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck disable=SC1091
source "${SCRIPT_DIR}/common.sh"

load_access_token
load_interview_id
OUTPUT_FILE="${TMP_DIR}/06-current-question.json"

echo "[6/8] get current question"

save_body "${OUTPUT_FILE}" GET "${BASE_URL}/api/interviews/${INTERVIEW_ID}/questions/current" \
  "Authorization:Bearer ${ACCESS_TOKEN}"

jq -r '.questionId' "${OUTPUT_FILE}" > "${QUESTION_ID_FILE}"
jq -r '.hasNext' "${OUTPUT_FILE}" > "${HAS_NEXT_FILE}"

echo "saved question id -> ${QUESTION_ID_FILE}"
echo "saved hasNext -> ${HAS_NEXT_FILE}"
echo "questionType: $(jq -r '.questionType' "${OUTPUT_FILE}")"
echo "hasNext: $(jq -r '.hasNext' "${OUTPUT_FILE}")"
