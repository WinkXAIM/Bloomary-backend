# Flowary-backend

## 로컬 실행 환경 설정

### 요구 사항

| 항목 | 버전 |
|------|------|
| Java | 25 |
| Spring Boot | 4.0.6 |
| MySQL | 8.x |
| Gradle | Wrapper 사용 (별도 설치 불필요) |

---

### 1. MySQL DB 설정

```sql
CREATE DATABASE bloomary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

root 비밀번호가 있다면 `application-local.yml`의 `DB_PASSWORD`에 입력합니다.

---

### 2. application-local.yml 생성

`src/main/resources/application-local.yml` 파일을 직접 생성합니다. (`.gitignore`에 포함되어 있으므로 git에 커밋되지 않습니다.)

```yaml
DB_HOST: localhost
DB_PORT: 3306
DB_NAME: bloomary
DB_PASSWORD: (MySQL root 비밀번호, 없으면 빈 값)

ai:
  server:
    url: http://localhost:8000

kakao:
  rest-api-key: (카카오 REST API 키)
  redirect-uri: http://127.0.0.1:8080/auth/kakao/callback
  clientRedirectUri: http://127.0.0.1:5174/home
  clientSecret: (카카오 Client Secret)

JWT_SECRET: (아래 명령어로 생성한 값)
```

#### JWT_SECRET 생성

터미널에서 아래 명령어를 실행해 안전한 시크릿을 생성합니다.

```bash
openssl rand -base64 64
```

출력된 값을 `JWT_SECRET`에 그대로 붙여넣습니다.

#### 카카오 OAuth 앱 설정

[카카오 개발자 콘솔](https://developers.kakao.com)에서 앱을 생성한 뒤:

- **REST API 키** → `rest-api-key`
- **Client Secret** → `clientSecret` (보안 탭에서 활성화 후 발급)
- Redirect URI에 `http://127.0.0.1:8080/auth/kakao/callback` 등록

---

### 3. 실행

```bash
./gradlew bootRun
```

---

## 협업 방식

이 프로젝트는 **GitHub Flow**를 기반으로 협업합니다.

1. `main` 브랜치는 항상 배포 가능한 상태를 유지합니다.
2. 새 작업은 `main`에서 브랜치를 생성합니다.
3. 작업이 완료되면 Pull Request를 열고 코드 리뷰를 진행합니다.
4. 리뷰 승인 후 `main`으로 병합합니다.
5. 병합 즉시 배포합니다.

---

## Git Convention

### 브랜치 네이밍

```
<type>/<issue-number>-<short-description>

예시:
feat/12-add-login-page
fix/34-fix-button-style
chore/56-update-dependencies
```

| 타입 | 설명 |
|------|------|
| `feat` | 새로운 기능 |
| `fix` | 버그 수정 |
| `refactor` | 리팩토링 |
| `style` | 코드 포맷, 스타일 변경 (기능 변경 없음) |
| `chore` | 빌드, 설정, 의존성 등 기타 작업 |
| `docs` | 문서 수정 |
| `test` | 테스트 코드 |

### 커밋 메시지

```
<type>: <subject>

예시:
feat: 로그인 페이지 추가
fix: 버튼 클릭 시 스타일 깨짐 수정
chore: eslint 설정 업데이트
```

- 제목은 **명령문 형태**로 작성합니다.
- 제목은 **50자 이내**로 작성합니다.
- 제목 끝에 마침표를 붙이지 않습니다.

### Pull Request

- PR 제목은 커밋 메시지 형식과 동일하게 작성합니다.
- 변경 사항, 구현 방법, 스크린샷(UI 변경 시)을 포함합니다.
- 최소 1명의 리뷰어 승인 후 병합합니다.
- 병합 후 작업 브랜치는 삭제합니다.