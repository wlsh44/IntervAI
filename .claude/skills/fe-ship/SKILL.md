---
name: fe-ship
description: 프런트엔드 기능 개발 워크플로우 [5]~[6] 실행 — 커밋 분리 + push + PR 생성
---

> 이 skill은 프런트 전용 진입점입니다. 실제 ship 규칙은 `.agent/skills/intervai-ship/SKILL.md`가 canonical입니다.
> 특히 push 전 테스트, `.github/PULL_REQUEST_TEMPLATE.md` 준수, 기존 PR 존재 시 한글 업데이트 코멘트 작성 규칙을 반드시 따릅니다.

## 사용법
```
/fe-ship
```

## 실행 흐름

### Step 1: 변경 내역 파악
먼저 `.agent/skills/intervai-ship/SKILL.md`와 `.github/PULL_REQUEST_TEMPLATE.md`를 읽는다.

```bash
git status --short
git diff --stat
```
변경/추가된 파일 목록을 확인한다.

### Step 2: Push 전 테스트
프런트 변경은 push 전에 반드시 실행한다:

```bash
cd front
npm run build
```

테스트가 실패하면 push/PR 생성을 중단하고 `.agent/skills/intervai-fe/SKILL.md` 기준으로 수정한 뒤 다시 테스트한다.

### Step 3: 논리적 단위로 그룹핑
파일을 아래 순서와 기준으로 그룹핑한다:

| 그룹 | 해당 파일 | 커밋 메시지 패턴 |
|------|----------|----------------|
| API 함수 | `features/{domain}/api/` | `feat: {domain} API 함수 추가` |
| Hooks | `features/{domain}/hooks/` | `feat: {domain} mutation hooks 추가` |
| 공통 컴포넌트 | `features/{domain}/components/`, `shared/components/` | `refactor: {domain} UI 컴포넌트 추출` |
| 페이지 | `features/{domain}/pages/` | `feat: {domain} 페이지 구현` |
| 설정/스타일 | `vite.config.ts`, `index.css`, `*.config.*` | `chore: {설명}` |
| 문서 | `*.md`, `CLAUDE.md` | `chore: {설명}` |

### Step 4: 커밋 목록 확인
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

### Step 5: Push
```bash
git push origin {현재 브랜치}
```

### Step 6: PR 생성 또는 확인
기존 PR 확인:
```bash
gh pr list --head {현재 브랜치}
```

PR이 없으면 `.github/PULL_REQUEST_TEMPLATE.md`를 따른 본문으로 생성한다:
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

이미 PR이 있으면 중복 생성하지 않고, 추가 작업 내용을 한글 코멘트로 작성한다:

```bash
gh pr comment {PR번호} --body "$(cat <<'EOF'
## 추가 작업 요약
- {이번 push에 포함된 변경}

## 변경 영역
- {주요 파일 또는 기능 영역}

## 테스트
- `npm run build` 통과

## 남은 리스크
- 없음
EOF
)"
```

PR URL을 출력하고 **"PR이 생성되었습니다. 리뷰 후 `/fe-review`를 실행하세요."** 안내.
