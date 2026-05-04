# Agent Traps

반복 실수 패턴 목록. 작업 시작 전 반드시 확인할 것.

| 패턴 | 잘못된 방법 | 올바른 방법 |
|---|---|---|
| 브랜치 없이 구현 시작 | main에서 파일 수정 후 커밋 | 첫 파일 수정 전 `git checkout -b feature/issue-{n}-backend` 먼저 실행 |

## 상세

### main 직접 커밋 (issue-21)

**발생 경위**: intervai-be skill 진입 후 feature 브랜치 생성 없이 바로 파일 수정을 시작하고 커밋. `git reset --hard`로 main을 복구하는 과정에서 커밋되지 않은 application layer 코드가 전부 소실되어 재작성 필요.

**예방 방법**: skill 진입 직후, 코드 탐색(Read/Bash)은 허용하지만 첫 Edit/Write 전에 반드시 feature 브랜치 체크아웃. docs/api.md 변경도 feature 브랜치에서 수행.
