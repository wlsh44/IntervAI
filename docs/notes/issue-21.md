# Issue #21 — 면접 히스토리 목록 조회 및 삭제

## 주요 결정사항

### 동적 필터: JpaSpecificationExecutor + EXISTS 서브쿼리 (백엔드)
- `JpaSpecificationExecutor<InterviewEntity>` 도입으로 동적 필터 지원
- keyword 필터: `interview_cs_subjects.topic` LIKE 검색 (EXISTS 서브쿼리)
- sessionStatus 필터: `interview_sessions.session_status` (EXISTS 서브쿼리)
- JOIN 대신 EXISTS를 선택한 이유: 1 interview : N cs_subjects 관계에서 JOIN 시 중복 row 발생 → 페이지네이션 COUNT 쿼리 오염 우려

### totalScore 조회: InterviewReportRepository 직접 사용 (백엔드)
- 사용자 지시로 InterviewReportFinder 대신 Repository 직접 주입
- `findByInterviewIdInAndStatus(List<Long>, EntityStatus)` 배치 조회로 N+1 방지
- InterviewFinder에서 cross-domain Repository 주입 (아키텍처 규칙 "우선 활용"이지 금지는 아님)

### keyword 검색 범위
- CS/ALL 유형: `interview_cs_subjects.topic` LIKE 검색
- PORTFOLIO 유형: CS topic 없음 → 키워드 검색 시 결과에서 제외됨 (의도된 동작)

### soft delete 구현
- `InterviewManager.delete()`: `InterviewEntity.delete()` (BaseEntity 제공) 호출
- 별도 cascade 처리 없음 — 세션/질문은 soft delete 미전파 (현재 범위 외)

### queryKeys.interviews.list() 파라미터 지원 (프론트엔드)
- 대시보드(size:3)와 히스토리(필터+페이지) 쿼리가 별도 캐시 키를 갖도록 분리
- `list()` → `['interviews', 'list', undefined]` / `list({...})` → `['interviews', 'list', {...}]`

### BE→FE 순차 작업 시 브랜치 전략 (프론트엔드)
- FE 브랜치를 BE 브랜치에서 분기하는 것은 백엔드 API를 로컬에서 테스트하기 위해 정상적인 흐름
- PR 생성 전 `git rebase --onto main feature/issue-{n}-backend` 로 BE 커밋을 제거한 뒤 생성

### 삭제 후 invalidateQueries 범위: interviews.all (프론트엔드)
- `interviews.all`로 상위 키 무효화 → 대시보드 recent history도 자동 갱신됨

## 포기한 접근법

- **@Query JPQL 동적 쿼리**: 선택적 파라미터를 NULL 체크로 처리하는 방식 고려했으나 가독성이 떨어지고 Specification이 더 확장 가능
- **InterviewReportFinder 사용**: 아키텍처 규칙에 맞지만 사용자가 Repository 직접 사용 명시적으로 요청

## 다음 작업 시 참고

- PORTFOLIO 유형 keyword 검색 미지원 — 의도된 동작. 추후 portfolio_links URL LIKE 검색 확장 가능
- soft delete 후 연관 데이터(세션, 질문) 처리 정책 미정 — 별도 이슈 검토 필요
- 기존 `InterviewChatScreen.tsx`에 pre-existing lint 에러(`setState in effect`) 존재 — 히스토리 작업 범위 밖, 별도 처리 필요
