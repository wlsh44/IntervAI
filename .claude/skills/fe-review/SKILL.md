---
name: fe-review
description: 프런트엔드 기능 개발 워크플로우 [7]~[8] 실행 — PR 리뷰 확인 + 수정 + resolve
---

## 실행 흐름

### Step 1: PR 조회
현재 브랜치의 열린 PR 번호를 조회한다:
```bash
gh pr list --head $(git branch --show-current) --json number,title
```

PR이 없으면 **"`/fe-ship`을 먼저 실행하여 PR을 생성하세요."** 안내 후 중단.

### Step 2: Review Threads 조회
GraphQL로 모든 review thread를 가져온다:
```bash
gh api graphql -f query='
{
  repository(owner: "wlsh44", name: "IntervAI") {
    pullRequest(number: {PR번호}) {
      reviewThreads(first: 50) {
        nodes {
          id
          isResolved
          comments(first: 3) {
            nodes {
              body
              path
              line
            }
          }
        }
      }
    }
  }
}'
```

### Step 3: Thread 분류
각 unresolved thread를 현재 코드와 비교하여 분류한다:

- **✅ 이미 반영됨**: 코드를 읽어 리뷰 내용이 이미 구현되어 있는 경우 → 즉시 resolve
- **🔧 수정 필요**: 아직 코드에 반영되지 않은 실질적 변경 요청
- **💬 논의 필요**: 설계/접근 방식에 대한 의견

분류 결과를 표 형태로 출력한다.

### Step 4: 수정 진행 여부 확인
"🔧 수정 필요" 항목이 있으면 목록을 보여주고 **수정 진행 여부를 사용자에게 확인**한다.

### Step 5: 수정 구현
수정 진행 시 `fe-plan-executor` 에이전트를 호출하여 각 수정 사항을 구현한다.
에이전트에 전달할 내용:
- 수정 필요 thread 내용 전체
- 관련 파일 경로
- 수정 후 `npm run typecheck` 실행 지시

### Step 6: Codex 코드 리뷰
수정 구현 완료 후 `codex:review --background`를 실행한다:
```bash
node "/Users/wlsh/.claude/plugins/cache/openai-codex/codex/1.0.3/scripts/codex-companion.mjs" review "--background"
```

### Step 7: Push + Resolve
```bash
git push origin {현재 브랜치}
```

수정된 thread들을 resolve한다:
```bash
gh api graphql -f query='
mutation {
  resolveReviewThread(input: {threadId: "{thread-id}"}) {
    thread { isResolved }
  }
}'
```

"✅ 이미 반영됨"으로 분류된 thread도 함께 resolve한다.

완료 후: **"리뷰 반영 및 resolve 완료. Codex 리뷰가 백그라운드에서 진행 중입니다. `/codex:status`로 확인하세요."**