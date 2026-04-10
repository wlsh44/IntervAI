---
name: profileId vs userId 불일치 이슈
description: 로그인/회원가입 응답에 profileId가 없어 프로필 API 호출 불가 — 백엔드 수정이 프런트 구현 선행 조건
type: project
---

로그인/회원가입(`POST /api/users/login`, `POST /api/users/sign-up`) 응답은 `{ id(=userId), nickname, accessToken }`만 반환하며 profileId를 포함하지 않는다.

프로필 API(`GET/PUT /api/profile/{profileId}`)는 path에 profileId가 필수이며, userId와 profileId는 백엔드 DB에서 별개 컬럼이다.

**Why:** 백엔드 회원가입 시 `profileManager.create(userId, EMPTY_PROFILE)`로 프로필이 자동 생성되지만, 생성된 profileId를 인증 응답에 포함하지 않는 설계 누락이다.

**How to apply:** 프로필 기능 구현 전 백엔드 담당자에게 로그인/회원가입 응답에 `profileId` 필드 추가를 요청해야 한다. 프런트는 `authStore`에 `profileId`를 저장하는 구조로 설계한다.
