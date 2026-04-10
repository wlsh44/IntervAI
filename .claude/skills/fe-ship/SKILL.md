---
name: fe-ship
description: 프런트엔드 기능 개발 워크플로우 [5]~[6] 실행 — 커밋 분리 + push + PR 생성
---

## 사용법
```
/fe-ship
```

## 실행 흐름

### Step 1: 변경 내역 파악
```bash
git status --short
git diff --stat
```
변경/추가된 파일 목록을 확인한다.

### Step 2: 논리적 단위로 그룹핑
파일을 아래 순서와 기준으로 그룹핑한다:

| 그룹 | 해당 파일 | 커밋 메시지 패턴 |
|------|----------|----------------|
| API 함수 | `features/{domain}/api/` | `feat: {domain} API 함수 추가` |
| Hooks | `features/{domain}/hooks/` | `feat: {domain} mutation hooks 추가` |
| 공통 컴포넌트 | `features/{domain}/components/`, `shared/components/` | `refactor: {domain} UI 컴포넌트 추출` |
| 페이지 | `features/{domain}/pages/` | `feat: {domain} 페이지 구현` |
| 설정/스타일 | `vite.config.ts`, `index.css`, `*.config.*` | `chore: {설명}` |
| 문서 | `*.md`, `CLAUDE.md` | `chore: {설명}` |

### Step 3: 커밋 목록 확인
그룹별 커밋 메시지를 사용자에게 보여주고 **확인을 받는다**.
확인 후 각 그룹을 순서대로 커밋한다 (Co-Authored-By 포함):
```bash
git add {파일들}
git commit -m "$(cat <<'EOF'
{커밋 메시지}

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
EOF
)"
```

### Step 4: Push
```bash
git push origin {현재 브랜치}
```

### Step 5: PR 생성 또는 확인
기존 PR 확인:
```bash
gh pr list --head {현재 브랜치}
```

PR이 없으면 생성한다:
```bash
gh pr create --base main --title "[FE] {도메인} 기능 구현" --body "$(cat <<'EOF'
## Summary
- {변경 요약 bullet points}

## Test plan
- [ ] `npm run typecheck` 통과
- [ ] `npm run lint` 통과
- [ ] 수동 시나리오 테스트

🤖 Generated with [Claude Code](https://claude.com/claude-code)
EOF
)"
```

PR URL을 출력하고 **"PR이 생성되었습니다. 리뷰 후 `/fe-review`를 실행하세요."** 안내.