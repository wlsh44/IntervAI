# CLAUDE.md — IntervAI Frontend

백엔드 API 문서: `../docs/api.md` | 도메인 컨텍스트: `../.claude/domain.md`
**아키텍처 & 개발 가이드**: `.claude/architecture.md`

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

```
[1] 요구사항(docs) 체크 -> 디자인(Stitch mcp) + 백엔드 현황(src/) 체크 -> dev-planner로 PLAN.md
[2] plan-executor → 브랜치 이동 -> frontend-developer → 구현
```

**dev-planner 호출 시 전달:**
- 도메인 문서: `docs/{도메인}.md`
- Stitch 화면 ID
- 백엔드 경로: `src/main/java/wlsh/project/intervai/{도메인}/`
- 브랜치: `fe/feat/{도메인}`
