#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

bash "${SCRIPT_DIR}/01-sign-up.sh"
bash "${SCRIPT_DIR}/02-login.sh"
bash "${SCRIPT_DIR}/03-create-interview.sh"
bash "${SCRIPT_DIR}/04-create-session.sh"
bash "${SCRIPT_DIR}/05-create-questions.sh"