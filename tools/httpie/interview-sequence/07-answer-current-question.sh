#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck disable=SC1091
source "${SCRIPT_DIR}/common.sh"

load_access_token
load_interview_id
load_question_id
load_has_next
OUTPUT_FILE="${TMP_DIR}/07-answer.json"

echo "[7/8] answer current question"

save_body "${OUTPUT_FILE}" POST "${BASE_URL}/api/interviews/${INTERVIEW_ID}/answers" \
  "Authorization:Bearer ${ACCESS_TOKEN}" \
  questionId:="${QUESTION_ID}" \
  content="${ANSWER_CONTENT}"

echo "feedback length: $(jq -r '.feedback | length' "${OUTPUT_FILE}")"
echo "hasNext from current question: ${HAS_NEXT}"
