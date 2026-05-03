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

## Manual Test Gate

After automated tests pass, output the following and **wait for user approval**. Do not proceed until 'approved' is received.

```
✋ 수동 테스트 준비 완료
백엔드: localhost:8080 / 프론트엔드: localhost:5173
테스트 후 'approved' 또는 피드백을 입력해주세요.
```

- 피드백 수신 시 → 해당 worktree 복귀 → 수정 → 자동 테스트 재실행 → 수동 테스트 게이트 재진입
- 'approved' 수신 시 → 다음 단계 진행

## Commit Cleanup

After manual test approval, clean up commits before pushing:

```bash
git rebase -i
```

- Squash WIP and temp commits
- Refine commit messages to match convention: `feat / fix / refactor / test / docs / chore`
- Do not mix backend, frontend, docs, and skill changes in one commit when they can be cleanly separated

## Workflow

1. Inspect changes:

```bash
git status --short
git diff --stat
```

2. Identify whether the work is backend, frontend, mixed, or docs/skill-only.
3. Run the required pre-push tests.
4. Run Manual Test Gate and wait for approval.
5. Run commit cleanup (`git rebase -i`).
6. Push the current branch after cleanup.
7. Check for an existing PR for the current branch:

```bash
gh pr list --head "$(git branch --show-current)" --json number,title,url
```

8. If no PR exists, create one per branch (backend and frontend PRs are always separate):

```bash
gh pr create --base develop --title "{Korean PR title}" --body-file "{prepared PR body file}"
```

- 백엔드: `feature/issue-{n}-backend → develop`
- 프론트엔드: `feature/issue-{n}-frontend → develop`
- **`main` 브랜치 직접 push 금지**
- **백엔드 + 프론트엔드 단일 PR 금지**

9. If a PR already exists, do not create a duplicate. Add a Korean comment summarizing the additional work:

```bash
gh pr comment {PR_NUMBER} --body-file "{prepared update comment file}"
```

## PR Body Rules

- Use the repository PR template as the source of truth.
- Include the related issue number when one exists.
- Include exact tests run and whether they passed.
- Keep descriptions in Korean unless the template explicitly requires otherwise.

## Existing PR Comment Rules

When updating an existing PR, the comment must be in Korean and include:

- 추가 작업 요약
- 변경된 주요 파일 또는 영역
- 실행한 테스트와 결과
- 남은 리스크 또는 없음

Keep the comment factual. Do not replace the existing PR body unless the user asks.

## Post-Ship: Document Update (필수)

After the PR is created or updated, run the following. Do not skip.

1. **Agent Traps 갱신**: 이번 작업에서 반복 실수한 패턴 발견 시
    - `CLAUDE.md`의 Agent Traps 표에 행 추가
    - `docs/agent-traps.md`에 상세 내용 추가
2. **작업 로그 작성**: `docs/notes/issue-{n}.md` 생성
    - 주요 결정사항 및 선택 이유
    - 포기한 접근법과 이유
    - 다음 작업 시 참고할 점
3. **참조 문서 최신화**:
    - `docs/api.md`: 실제 구현 스펙과 일치 여부 확인
    - `docs/**`: 전체 순회하여 변동 확인 및 문서 부패화 제거
    - `CLAUDE.md`: 워크플로우 변경사항 및 새 규칙·예외 케이스 반영
4. Commit: `docs: update CLAUDE.md and references after #{n}`
