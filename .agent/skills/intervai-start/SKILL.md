---
name: intervai-start
description: Use at the beginning of IntervAI work to turn the user's prompt into a GitHub issue or verify an existing matching issue, using the repository issue templates under .github/ISSUE_TEMPLATE.
---

# IntervAI Start Skill

Use this skill before implementation when the user asks to start work from a prompt, create/check an issue, or prepare tracked work.

## Required Templates

Always read `.github/ISSUE_TEMPLATE/` before creating an issue. The issue body must follow one of those templates exactly:

- Feature or improvement: `.github/ISSUE_TEMPLATE/✨-feature.md`
- Bug fix: `.github/ISSUE_TEMPLATE/🐛-fix.md`
- Refactor: `.github/ISSUE_TEMPLATE/♻️-refactor.md`
- Docs: `.github/ISSUE_TEMPLATE/docs.md`

Do not invent a different issue structure. Keep the template headings and checklist style.

## Pre-Work Mandatory Read

Before doing anything else, read these files and output a 1–2 line summary of each.
Do not proceed until this step is complete.

1. `CLAUDE.md` — Agent Traps 표 확인
2. `docs/agent-traps.md` — Agent Traps 표에 항목이 있을 때만
3. `docs/api.md` — API 변경·추가가 포함된 작업일 때만
4. `docs/architecture.md` — 새 도메인·엔티티 추가 작업일 때만
5. `docs/notes/issue-{관련 이슈 번호}.md` — 연관 이슈가 명시된 경우만

출력 형식:
```
[필독 완료]
- Agent Traps: {확인한 패턴 수}개 확인
- api.md: {읽었으면 핵심 변경 영향 1줄 / 스킵이면 "해당 없음"}
- architecture.md: {읽었으면 관련 도메인 1줄 / 스킵이면 "해당 없음"}
```

## Plan Mode Trigger

After reading required files, judge whether Plan Mode is required.
Enter Plan Mode if **any** of the following apply:

- 수정 예상 파일 3개 이상
- 새 엔티티 또는 DB 테이블 추가
- `docs/api.md` 스펙 변경 필요
- 백엔드 + 프론트엔드 동시 작업
- 처음 접하는 도메인 또는 모듈

**Plan Mode output** (read-only exploration, no edits):
- 영향 받는 파일 목록
- 작업 단계 순서 (의존성 포함)
- 예상 커밋 단위

For complex plans, suggest: "별도 Claude 세션에서 스태프 엔지니어 역할로 이 계획을 검토하세요."

→ **Wait for user approval before any implementation.**

## Task Decomposition Trigger

Propose decomposition if **any** of the following apply. Do not auto-create issues.

- 예상 커밋 5개 초과
- 계획에 없던 파일 수정 필요
- 단일 PR에 백엔드·프론트엔드 변경 혼재

→ Output decomposition proposal and **wait for user approval.**

## Workflow

1. Run Pre-Work Mandatory Read above.
2. Summarize the user's prompt into a short Korean issue title.
3. Classify the work as feature, fix, refactor, or docs and select the matching template.
4. Check whether a matching issue already exists:

```bash
gh issue list --state open --search "{keywords}" --json number,title,labels,url
gh issue list --state all --search "{keywords}" --json number,title,state,labels,url
```

5. If a matching issue exists, use it and report the issue number and URL.
6. If a matching closed issue exists, only reuse it when the work is clearly a continuation; otherwise create a new issue and reference the closed one under `## 참고 사항(선택)`.
7. If no matching issue exists, create one with the selected template:

```bash
gh issue create --title "{Korean title}" --label "{template label}" --body-file "{prepared issue body file}"
```

8. Run Plan Mode trigger judgment.
9. If `docs/api.md` change is needed, modify and commit before branching:
   `docs: update api spec for #{n}`
   → **사용자 승인 없이 `docs/api.md` 변경 금지**

## Issue Body Rules

- Fill `## 이슈 내용` with the user's requirement in Korean.
- Fill `## 작업 내용` with concrete checklist items.
- Use `## 참고 사항(선택)` for constraints, related files, API notes, or known risks.
- Keep the assigned template label from frontmatter: `enhancement`, `fix`, `refactor`, or `docs`.
- If the prompt is ambiguous, create a minimal issue with assumptions listed under `## 참고 사항(선택)`.

## After The Issue

- For backend implementation, use `.agent/skills/intervai-be/SKILL.md`.
- For frontend implementation, use `.agent/skills/intervai-fe/SKILL.md`.
- For mixed work, split the plan by backend and frontend, but keep one issue if the user prompt describes one coherent task.
- Branch naming: backend `feature/issue-{n}-backend` / frontend `feature/issue-{n}-frontend`
- Use `claude --worktree` flag for isolated worktree per branch.

