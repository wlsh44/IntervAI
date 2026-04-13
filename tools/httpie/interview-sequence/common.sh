#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${SCRIPT_DIR}/.env"
TMP_DIR="${SCRIPT_DIR}/tmp"
SESSION_FILE="${TMP_DIR}/session.json"
ACCESS_TOKEN_FILE="${TMP_DIR}/access-token.txt"
INTERVIEW_ID_FILE="${TMP_DIR}/interview-id.txt"
SESSION_ID_FILE="${TMP_DIR}/session-id.txt"
QUESTION_ID_FILE="${TMP_DIR}/question-id.txt"
HAS_NEXT_FILE="${TMP_DIR}/has-next.txt"

if [[ ! -f "${ENV_FILE}" ]]; then
  echo ".env 파일이 없습니다. env.example을 복사해서 ${ENV_FILE} 을 생성하세요." >&2
  exit 1
fi

# shellcheck disable=SC1090
source "${ENV_FILE}"

mkdir -p "${TMP_DIR}"

require_command() {
  local cmd="$1"
  if ! command -v "${cmd}" >/dev/null 2>&1; then
    echo "필수 명령이 없습니다: ${cmd}" >&2
    exit 1
  fi
}

require_command http
require_command jq

save_body() {
  local output_file="$1"
  shift
  http --check-status --ignore-stdin "$@" | tee "${output_file}"
}

save_auth_body() {
  local output_file="$1"
  shift
  http --check-status --ignore-stdin --session="${SESSION_FILE}" "$@" | tee "${output_file}"
}

load_access_token() {
  if [[ ! -f "${ACCESS_TOKEN_FILE}" ]]; then
    echo "access token 파일이 없습니다. 01 또는 02 단계를 먼저 실행하세요." >&2
    exit 1
  fi
  ACCESS_TOKEN="$(cat "${ACCESS_TOKEN_FILE}")"
}

load_interview_id() {
  if [[ ! -f "${INTERVIEW_ID_FILE}" ]]; then
    echo "interviewId 파일이 없습니다. 03 단계를 먼저 실행하세요." >&2
    exit 1
  fi
  INTERVIEW_ID="$(cat "${INTERVIEW_ID_FILE}")"
}

load_question_id() {
  if [[ ! -f "${QUESTION_ID_FILE}" ]]; then
    echo "questionId 파일이 없습니다. 06 단계를 먼저 실행하세요." >&2
    exit 1
  fi
  QUESTION_ID="$(cat "${QUESTION_ID_FILE}")"
}

load_has_next() {
  if [[ ! -f "${HAS_NEXT_FILE}" ]]; then
    echo "hasNext 파일이 없습니다. 06 단계를 먼저 실행하세요." >&2
    exit 1
  fi
  HAS_NEXT="$(cat "${HAS_NEXT_FILE}")"
}
