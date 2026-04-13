#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck disable=SC1091
source "${SCRIPT_DIR}/common.sh"

load_access_token
OUTPUT_FILE="${TMP_DIR}/03-create-interview.json"

echo "[3/8] create interview"

save_body "${OUTPUT_FILE}" POST "${BASE_URL}/api/interviews" \
  "Authorization:Bearer ${ACCESS_TOKEN}" \
  interviewType="${INTERVIEW_TYPE}" \
  difficulty="${DIFFICULTY}" \
  questionCount:="${QUESTION_COUNT}" \
  interviewerTone="${INTERVIEWER_TONE}" \
  csSubjects:='[
    {
      "category": "'"${CS_CATEGORY_1}"'",
      "topics": ["'"${CS_TOPIC_1A}"'", "'"${CS_TOPIC_1B}"'"]
    },
    {
      "category": "'"${CS_CATEGORY_2}"'",
      "topics": ["'"${CS_TOPIC_2A}"'", "'"${CS_TOPIC_2B}"'"]
    }
  ]'

jq -r '.id' "${OUTPUT_FILE}" > "${INTERVIEW_ID_FILE}"

echo "saved interview id -> ${INTERVIEW_ID_FILE}"
