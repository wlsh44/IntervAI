#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck disable=SC1091
source "${SCRIPT_DIR}/common.sh"

bash "${SCRIPT_DIR}/01-sign-up.sh" || true
bash "${SCRIPT_DIR}/02-login.sh"
bash "${SCRIPT_DIR}/03-create-interview.sh"
bash "${SCRIPT_DIR}/04-create-session.sh"
bash "${SCRIPT_DIR}/05-create-questions.sh"

round=1
max_rounds="${MAX_QUESTION_ROUNDS:-60}"

while (( round <= max_rounds )); do
  echo "question round ${round}/${max_rounds}"
  bash "${SCRIPT_DIR}/06-get-current-question.sh"
  current_has_next="$(cat "${HAS_NEXT_FILE}")"
  bash "${SCRIPT_DIR}/07-answer-current-question.sh"

  if [[ "${current_has_next}" == "false" ]]; then
    echo "last question answered. finish session."
    bash "${SCRIPT_DIR}/08-finish-session.sh"
    echo "interview sequence flow completed"
    exit 0
  fi

  round=$((round + 1))
done

echo "max question rounds exceeded: ${max_rounds}" >&2
echo "무한 반복 방지를 위해 실행을 중단했습니다. questionCount 또는 hasNext 동작을 확인하세요." >&2
exit 1
