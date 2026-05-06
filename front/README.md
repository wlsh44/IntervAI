# IntervAI Frontend

IntervAI의 프론트엔드 애플리케이션입니다.  
사용자 인증, 프로필 관리, 면접 설정, AI 면접 진행, 히스토리와 결과 리포트 화면을 제공합니다.

> 이미지 추가 추천: 이 위치에 대시보드, 면접 진행 화면, 결과 리포트 화면 스크린샷을 배치하면 좋습니다.

## 주요 기능

- 회원가입 및 로그인
- 대시보드와 최근 면접 기록 조회
- 프로필, 기술 스택, 포트폴리오 링크 관리
- 면접 유형, 난이도, 질문 수, 면접관 톤 설정
- 채팅형 면접 진행 및 답변 제출
- 답변 피드백, 세션 종료, 결과 리포트 확인

## 기술 스택

- React
- TypeScript
- Vite
- React Router
- Zustand
- TanStack Query
- Axios
- React Hook Form
- Zod
- Tailwind CSS
- lucide-react

## 프로젝트 구조

```text
front
├── src
│   ├── app              # 앱 엔트리와 라우터
│   ├── features
│   │   ├── auth         # 인증
│   │   ├── dashboard    # 대시보드
│   │   ├── history      # 면접 히스토리
│   │   ├── interview    # 면접 설정 및 진행
│   │   └── profile      # 프로필 관리
│   └── shared           # 공통 API, 레이아웃, UI, 타입
└── package.json
```

> 다이어그램 추가 추천: 로그인 → 프로필 입력 → 면접 설정 → 채팅 진행 → 결과 리포트 흐름은 간단한 화면 플로우 다이어그램으로 정리하기 좋습니다.

## 로컬 실행

```bash
npm install
npm run dev
```

기본 개발 서버는 Vite 설정을 따릅니다.

## 테스트 및 빌드

```bash
npm run typecheck
npm run lint
npm run build
```
