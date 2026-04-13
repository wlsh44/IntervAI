#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="tools/httpie/interview-sequence"

bash "${SCRIPT_DIR}/06-get-current-question.sh"
bash "${SCRIPT_DIR}/07-answer-current-question.sh"