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

## Workflow

1. Summarize the user's prompt into a short Korean issue title.
2. Classify the work as feature, fix, refactor, or docs and select the matching template.
3. Check whether a matching issue already exists. Search open issues first, then all issues if needed:

```bash
gh issue list --state open --search "{keywords}" --json number,title,labels,url
gh issue list --state all --search "{keywords}" --json number,title,state,labels,url
```

4. If a matching issue exists, use it and report the issue number and URL.
5. If a matching closed issue exists, only reuse it when the work is clearly a continuation; otherwise create a new issue and reference the closed one under `## 참고 사항(선택)`.
6. If no matching issue exists, create one with the selected template:

```bash
gh issue create --title "{Korean title}" --label "{template label}" --body-file "{prepared issue body file}"
```

7. Mention the issue number in the work plan and branch name when useful.

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
