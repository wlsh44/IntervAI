# 사용자 프로필

## 개요
- 목적: 사용자의 기본 정보(직군, 경력, 기술 스택, 포트폴리오 링크)를 관리하여 AI 면접 세션 생성 시 맞춤형 설정의 기본값으로 활용한다.
- 대상 사용자: 회원가입 후 면접 연습을 시작하려는 사용자
- 관련 개발 단계: Stage 2 (완료)

## 진행 상황

| 단계 | 상태 |
|------|------|
| Stage 2 — 사용자 프로필 | ✅ 완료 |

## 요구사항
### 기능 요구사항
- [x] 프로필 생성 (회원가입 후 userId 기반 자동 생성 또는 수동 생성)
- [x] 프로필 조회 (본인만 가능, userId 기반)
- [x] 프로필 수정 (본인만 가능, profileId 기반)
- [x] 목표 직군 설정 (FRONTEND / BACKEND / FULLSTACK / ANDROID / IOS / DEVOPS / DATA_ENGINEER / ML_ENGINEER)
- [x] 경력 수준 설정 (ENTRY / JUNIOR / SENIOR)
- [x] 기술 스택 태그 등록 (다대다 관계, 기존 스택 재사용)
- [x] 포트폴리오 링크 등록 (1:N 관계, URL 형태)
- [x] 본인 프로필 접근 검증 — 타인 접근 시 403 반환
- [ ] 닉네임 수정 (범위 외 — 현재 계획 없음)
- [ ] 프로필 사진 업로드 (AWS S3 연동)
- [ ] GitHub URL 연동을 통한 레포지토리 목록 파싱

### 비기능 요구사항
- [x] 기술 스택은 공유 테이블(`TechStack`)로 관리하여 중복 데이터 방지
- [ ] 프로필 사진은 AWS S3에 저장하고 URL만 DB에 보관
- [ ] GitHub API 호출은 실패해도 서비스에 영향을 주지 않도록 예외 처리

## 사용자 스토리
- As a 로그인된 사용자, I want to 내 직군과 경력 수준을 설정하고 싶다 so that 면접 세션 생성 시 자동으로 기본값이 적용된다.
- As a 로그인된 사용자, I want to 내 기술 스택과 포트폴리오 링크를 등록하고 싶다 so that AI가 나에게 맞는 질문을 생성할 수 있다.
- As a 로그인된 사용자, I want to 내 프로필만 조회/수정할 수 있고 싶다 so that 다른 사용자가 내 정보에 접근할 수 없다.

## 사용자 플로우
1. 사용자가 로그인 후 프로필 설정 페이지로 이동한다.
2. 목표 직군, 경력 수준을 선택한다.
3. 기술 스택을 태그 형식으로 입력/선택한다.
4. GitHub URL 또는 포트폴리오 링크를 입력한다.
5. 저장 버튼 클릭 시 프로필이 업데이트된다.
6. 이후 면접 세션 생성 시 프로필 데이터가 기본값으로 자동 적용된다.

## 수용 기준 (Acceptance Criteria)
- [x] `POST /api/users/profile` — 프로필 생성 성공 (userId 기반)
- [x] `GET /api/users/profile` — 본인 프로필 조회 성공 (userId 기반)
- [x] `PUT /api/users/profile` — 본인 프로필 수정 성공 (userId 기반)
- [x] 타인의 프로필 접근 차단 (accessToken의 userId로 검증)
- [x] 기술 스택 등록 시 동일한 이름의 스택이 이미 존재하면 새로 생성하지 않고 재사용
- [ ] 프로필 사진 업로드 및 S3 저장 성공
- [ ] GitHub URL 입력 시 레포지토리 목록 파싱 성공

## 범위 외 (Out of Scope)
- 다른 사용자 프로필 공개 조회
- 프로필 공개/비공개 설정
- GitHub 레포지토리 상세 분석 (LLM 프롬프트 주입은 AI 면접 기능에서 처리)

## 미결 사항 (Open Questions)
- GitHub URL 연동 시 레포지토리 파싱 범위 결정 (전체 레포 vs 선택 레포)
- 프로필 사진 크기 및 형식 제한 정책
- 프로필이 없는 신규 사용자를 위한 기본 프로필 자동 생성 여부

## 관련 도메인/엔티티
- `Profile` — id, userId (unique), jobCategory, careerLevel, techStacks, portfolioLinks
- `TechStack` — id, name (unique, max 100) — 공유 기술 스택
- `ProfileTechStack` — id, profileId, techStackId — 다대다 매핑 테이블
- `PortfolioLink` — id, profileId, url (max 500) — 포트폴리오 URL
- `JobCategory`: FRONTEND, BACKEND, FULLSTACK, ANDROID, IOS, DEVOPS, DATA_ENGINEER, ML_ENGINEER
- `CareerLevel`: ENTRY, JUNIOR, SENIOR