---
name: Enum 패키지 위치 패턴
description: 도메인 공유 Enum은 common.domain 패키지로 이동해야 함 — profile.domain.JobCategory가 interview 도메인으로 확장될 때 발견
type: project
---

`JobCategory` enum이 `profile.domain` 패키지에만 정의되어 있었으나, `interview` 도메인에서도 필요해짐.

**Why:** 특정 도메인 패키지에 정의된 enum을 다른 도메인이 직접 참조하면 계층 경계가 모호해지고, 하나의 도메인이 다른 도메인에 구조적으로 의존하게 됨.

**How to apply:** 두 개 이상의 도메인이 동일한 enum을 사용해야 할 경우, `common/domain/` 패키지로 이동하고 양쪽에서 import. 새 도메인에서 이미 다른 도메인의 enum을 참조하는 코드를 발견하면 이 패턴을 바로 적용할 것.
