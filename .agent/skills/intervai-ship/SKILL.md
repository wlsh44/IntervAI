---
name: intervai-ship
description: Use at the end of IntervAI work to test, commit, push, and create or update a GitHub PR, using .github/PULL_REQUEST_TEMPLATE.md and adding Korean update comments when a PR already exists.
---

# IntervAI Ship Skill

Use this skill when the user asks to ship, commit/push, open a PR, or update an existing PR after implementation.

## Required Template

Always read `.github/PULL_REQUEST_TEMPLATE.md` before creating or updating a PR.

- If the template has content, the PR body must follow that template.
- If the template is empty, write a Korean PR body with `## Summary` and `## Test plan`, then update this skill's behavior once the template gains content.
- Do not create a PR body that ignores a non-empty template.

## Pre-Push Test Gate

Run tests before pushing. Choose the check by changed area:

- Backend changes: `./gradlew test`
- Frontend changes under `front/`: from `front/`, run `npm run build`
- Mixed frontend and backend changes: run both checks
- Docs/skill-only changes: run `git diff --check`

If a required test fails, stop shipping. Use the matching implementation skill to fix the work, then rerun the failed tests:

- Backend failure: use `.agent/skills/intervai-be/SKILL.md`
- Frontend failure: use `.agent/skills/intervai-fe/SKILL.md`

Do not push while the required test gate is failing.

## Workflow

1. Inspect changes:

```bash
git status --short
git diff --stat
```

2. Identify whether the work is backend, frontend, mixed, or docs/skill-only.
3. Run the required pre-push tests.
4. Split commits by logical unit. Do not mix unrelated frontend, backend, docs, and skill changes when they can be cleanly separated.
5. Push the current branch after tests pass.
6. Check for an existing PR for the current branch:

```bash
gh pr list --head "$(git branch --show-current)" --json number,title,url
```

7. If no PR exists, create one using `.github/PULL_REQUEST_TEMPLATE.md`:

```bash
gh pr create --base main --title "{Korean PR title}" --body-file "{prepared PR body file}"
```

8. If a PR already exists, do not create a duplicate. Add a Korean comment summarizing the additional work:

```bash
gh pr comment {PR_NUMBER} --body-file "{prepared update comment file}"
```

## Existing PR Comment Rules

When updating an existing PR, the comment must be in Korean and include:

- 추가 작업 요약
- 변경된 주요 파일 또는 영역
- 실행한 테스트와 결과
- 남은 리스크 또는 없음

Keep the comment factual. Do not replace the existing PR body unless the user asks.

## PR Body Rules

- Use the repository PR template as the source of truth.
- Include the related issue number when one exists.
- Include exact tests run and whether they passed.
- Keep descriptions in Korean unless the template explicitly requires otherwise.
