---
name: fe-implement
description: 프런트엔드 기능 개발 워크플로우 [3]~[4] 실행 — 구현 + 코드 리뷰
---

## 실행 흐름

### Step 1: PLAN.md 확인
`front/PLAN.md`가 존재하는지 확인한다.
없으면 중단하고 **"`/fe-start {domain} {screen-id}`를 먼저 실행하세요."** 안내.

### Step 2: front/PLAN.md 직접 구현
에이전트 호출 없이 `.agent/skills/intervai-fe/SKILL.md` 기준으로 `front/PLAN.md`를 단계별로 구현한다.

구현 중:
- 주요 단계 완료 후 `npm run typecheck` 실행
- 오류 시 수정 후 재검증
- 모든 단계 완료 후 `npm run lint` 실행

### Step 3: Codex 코드 리뷰 실행
구현 완료 후 자동으로 `codex:review --background`를 실행한다:
```bash
node "/Users/wlsh/.claude/plugins/cache/openai-codex/codex/1.0.3/scripts/codex-companion.mjs" review "--background"
```
백그라운드로 실행하고 **"Codex review started in the background. Check `/codex:status` for progress."** 안내.

### Step 4: 완료 안내
다음 단계 안내: **"구현 및 코드 리뷰가 진행 중입니다. `/codex:status`로 리뷰 결과를 확인하고, `/fe-ship`으로 커밋 및 PR을 생성하세요."**
