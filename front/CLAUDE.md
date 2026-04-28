# CLAUDE.md — IntervAI Frontend

백엔드 API 문서: `../docs/api.md`
**아키텍처 & 개발 가이드**: `../docs/architecture.md`

---

## 기술 스택

Vite + React 19 + TypeScript (strict) · Tailwind CSS + shadcn/ui
TanStack Query v5 · Zustand v5 · axios · react-router-dom v7 · react-hook-form + zod

---

## 브랜치 전략

| 유형 | 패턴 | 예시 |
|------|------|------|
| 기능 | `fe/feat/{도메인}` | `fe/feat/auth` |
| 버그 | `fe/fix/{도메인}` | `fe/fix/chat` |
| 리팩터링 | `fe/refactor/{도메인}` | `fe/refactor/profile` |
---

## 개발 명령어

```bash
npm run dev        # 개발 서버 (5173)
npm run build
npm run lint
npm run typecheck  # tsc --noEmit
```

---

## 개발 우선순위

1. 공통 인프라 (httpClient · enums · queryKeys · PrivateRoute · AppLayout)
2. 인증 — `fe/feat/auth`
3. 프로필 — `fe/feat/profile`
4. 면접 플로우 — `fe/feat/interview`

---

## 기능 개발 워크플로우

> 현재 프런트 작업 기준은 `../.agent/skills/intervai-fe/SKILL.md`입니다.
> Claude에서 작업할 때도 에이전트 호출 대신 해당 skill과 현재 문서를 기준으로 직접 계획/구현/검증합니다.
> 작업 시작과 종료는 각각 `../.agent/skills/intervai-start/SKILL.md`, `../.agent/skills/intervai-ship/SKILL.md`를 따릅니다.

```
[1] 요구사항(docs) 체크 -> 디자인(Stitch mcp) 조회
[2] docs/api.md 조회 -> front/PLAN.md 직접 작성
[3] 브랜치 이동(fe/feat/{도메인})
[4] intervai-fe skill 기준으로 직접 구현 -> 코드 리뷰(/codex:review --background 사용 가능)
[5] intervai-ship skill 기준으로 테스트 → 커밋 → git push
[6] gh pr create 또는 기존 PR 코멘트 업데이트 (base: main)
[7] PR 리뷰 확인 → 수정 필요 여부 판단 (필요 없으면 코멘트 전부 resolve 처리) 
[8] 수정할 경우 [3], [4] 다시 진행 → push 및 해결된 코멘트 resolve
```

**front/PLAN.md 작성 시 포함할 정보:**
- 도메인 문서: `docs/{도메인}.md`
- Stitch 화면 ID
- 백엔드 경로: `src/main/java/wlsh/project/intervai/{도메인}/`
- 브랜치: `fe/feat/{도메인}`

**참고자료**
./GITHUB.md : 프런트 브랜치 전략, PR 전략
