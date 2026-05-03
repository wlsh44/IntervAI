---
name: InterviewSummary 아키텍처 위반 현황
description: InterviewSummary domain 객체가 InterviewEntity(infra)를 직접 import하는 위반 존재
type: project
---

`src/main/java/wlsh/project/intervai/interview/domain/InterviewSummary.java`의 `of(InterviewEntity, SessionStatus)` 팩터리 메서드가 `InterviewEntity`를 파라미터로 받고 있어 domain → infra 참조 위반.

기존에 `of(Long, InterviewType, ...)` 오버로드도 있으므로 Entity를 받는 팩터리를 제거하고 Projection 또는 필드 직접 받는 방식으로 교체 가능.

**Why:** InterviewFinder.findSummaries()가 InterviewEntity를 직접 map()에서 사용하면서 InterviewSummary.of(entity, ...) 팩터리가 생겨났음.

**How to apply:** 히스토리 기능 작업(feat/history) 시 findSummaries() 리팩터링할 때 함께 수정 필요.
