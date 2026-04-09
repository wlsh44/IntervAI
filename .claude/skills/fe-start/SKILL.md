# /fe-start

프런트엔드 기능 개발 워크플로우 [1]~[2] 실행 — 디자인 조회 + 개발 플랜 생성

## 사용법
```
/fe-start {domain} {stitch-screen-id}
```

예시:
```
/fe-start profile 3e0a247492424efb8ffd7092822adee6
```

## 실행 흐름

### Step 1: 브랜치 이동
현재 브랜치가 `fe/feat/{domain}`이 아니면 이동한다.
```bash
git checkout fe/feat/{domain} 2>/dev/null || git checkout -b fe/feat/{domain}
```

### Step 2: 문서 읽기
다음 파일을 읽어 요구사항을 파악한다:
- `docs/{domain}.md` (없으면 `docs/api.md`에서 관련 섹션 검색)
- `docs/api.md` — 관련 API 엔드포인트

### Step 3: Stitch 디자인 조회
`mcp__stitch__get_screen` 으로 스크린 정보를 가져온다:
- project ID: `6025092785962214042`
- screen ID: 인자로 받은 `{stitch-screen-id}`

스크린 HTML의 `downloadUrl`을 `WebFetch`로 fetch하여 다음을 추출한다:
- 색상값, 폰트, 레이아웃 구조
- 입력 필드 / 버튼 / 레이블 텍스트
- 카드/컨테이너 스타일

### Step 4: fe-dev-planner 에이전트 호출
다음 정보를 전달하여 `fe-dev-planner` 에이전트를 호출한다:
- 도메인명: `{domain}`
- 도메인 문서 경로: `docs/{domain}.md`
- Stitch 스크린 ID + 추출된 디자인 요약
- 백엔드 관련 코드 경로: `src/main/java/wlsh/project/intervai/{domain}/`
- 프런트 피처 경로: `front/src/features/{domain}/`
- 출력 위치: `front/PLAN.md`

### Step 5: 플랜 요약 출력
`front/PLAN.md` 생성 후:
- 구현 필요 항목 목록 출력
- 생성/수정 파일 목록 출력
- 다음 단계 안내: **"플랜을 확인하고 `/fe-implement`를 실행하세요."**