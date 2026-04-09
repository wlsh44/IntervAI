**브랜치 규칙 (필수)**
- 프런트 작업 전 반드시 현재 브랜치 확인
- `fe/` 접두사 브랜치인지 검증 (`feat/`는 백엔드 전용)


**PR 리뷰 확인 명령어:**
```bash
gh api repos/wlsh44/IntervAI/pulls/{pr_number}/comments --jq '.[] | "FILE: \(.path)\nLINE: \(.line)\nBODY:\n\(.body)\n---"'
gh api repos/wlsh44/IntervAI/pulls/{pr_number}/reviews
```

**PR 리뷰**
- PR Title의 경우 백엔드와 구분할 수 있게 `[FE] ` 를 접두사로 붙임 
