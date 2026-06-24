# 백엔드 서버 API 명세

## 엔드포인트 목록

| 엔드포인트 | 메서드 | 인증 | 설명 |
| --- | --- | --- | --- |
| `/auth/kakao` | GET | X | 카카오 로그인 페이지로 리다이렉트 |
| `/auth/kakao/callback` | GET | X | 카카오 인증 콜백 — JWT 발급 |
| `/auth/signout` | POST | O | 로그아웃 |
| `/flowers` | POST | O | 꽃다발 이미지에서 꽃 종류 분류 |
| `/analyses` | POST | O | 꽃말 문장 생성 및 분석 결과 저장 |
| `/analyses` | GET | O | 분석 결과 목록 조회 |
| `/analyses/:id` | GET | O | 분석 결과 상세 조회 |
| `/analyses/:id` | DELETE | O | 분석 결과 삭제 |
| `/recommendations` | POST | O | 상황 기반 꽃다발 추천 |

> 인증이 필요한 API는 `Authorization: Bearer {accessToken}` 헤더를 포함해야 합니다.

AI는 snake_case, 백엔드는 camelCase 유의

---

## 인증 (Auth)

> 아직 카카오 OAuth 문서를 못 읽음!!! 정확히 로직 파악 후에 최신화 예정 ㅠ

### GET `/auth/kakao` — 카카오 로그인 페이지로 리다이렉트

요청/응답 바디 없음. 카카오 OAuth 인증 페이지로 302 리다이렉트

---

### GET `/auth/kakao/callback` — 카카오 인증 콜백

**Query Params**

| 항목 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `code` | String | 필수 | 카카오에서 발급한 인가 코드 |

**Response Body**

| 항목 | 타입 | 설명 |
| --- | --- | --- |
| `accessToken` | String | JWT Access Token |
| `refreshToken` | String | JWT Refresh Token |

> `accessToken` 및 `refreshToken`을 클라이언트는 **httpOnly 쿠키에 저장**

> `accessToken` 갱신은 accessToken 만료, refreshToken 유효 시에 백엔드에서 자동으로 재발급

---

### POST `/auth/signout` — 로그아웃

요청/응답 바디 없음. Access Token 무효화 처리.

---

## 꽃 분류 (Flowers)

### POST `/flowers` — 꽃다발 이미지에서 꽃 종류 분류

**Content-Type:** `multipart/form-data`

**Request Body**

| 항목 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `image` | File (multipart) | 필수 | 꽃다발 이미지 |

**Response Body**

| 항목 | 타입 | 설명 |
| --- | --- | --- |
| `flowers` | Array\<Object\> | 인식된 꽃 목록 |
| `flowers[].nameKo` | String | 꽃 이름(국문) |
| `flowers[].nameEn` | String | 꽃 이름(영문) |
| `flowers[].meaning` | String | 꽃말(국문) |
| `flowers[].box2d` | Array\<Integer\> | 바운딩 박스 좌표 [x1, y1, x2, y2] |

---

## 분석 결과 (Analyses)

### POST `/analyses` — 꽃말 생성 및 분석 결과 저장

**Content-Type:** `multipart/form-data`

**Request Body**

| 항목 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
|  |  |  |  |

**Response Body**

| 항목 | 타입 | 설명 |
| --- | --- | --- |
| `id` | String | 분석 결과 ID |
| `summary` | String | 분석 결과 요약 |
| `content` | String | 꽃말 조합 감성 문구(국문) |
| `story` | String | 스토리 미리보기 문구(영문) |
| `flowersMeanings` | Array\<Object\> | 개별 꽃말 목록 |
| `flowerMeanings[].nameKo` | String | 꽃 이름(국문) |
| `flowerMeanings[].nameEn` | String | 꽃 이름(영문) |
| `flowersMeanings[].meaning` | String | 꽃말 |
| `createdAt` | String (ISO 8601) | 생성 일시 |

---

### GET `/analyses` — 분석 결과 목록 조회

**Query Params:** `page` (default 1), `size` (default 10)

**Response Body**

| 항목 | 타입 | 설명 |
| --- | --- | --- |
| `analyses` | Array\<Object\> | 분석 결과 목록 |
| `analyses[].id` | String | 분석 결과 ID |
| `analyses[].summary` | String | 꽃말 요약 문장 |
| `analyses[].imageUrl` | String | 썸네일 이미지 URL |
| `analyses[].createdAt` | String (ISO 8601) | 생성 일시 |
| `total` | Integer | 분석 결과 개수 |
| `hasNextPage` | Boolean | 다음 페이지 존재 여부 |

---

### GET `/analyses/:id` — 분석 결과 상세 조회

**Response Body**

| 항목 | 타입 | 설명 |
| --- | --- | --- |
| `id` | String | 분석 결과 ID |
| `summary` | String | 분석 결과 요약 |
| `content` | String | 꽃말 조합 문장 |
| `imageUrl` | String | 이미지 URL |
| `story` | String \| null | 생성된 스토리 (없으면 null) |
| `flowers` | Array\<Object\> | 꽃 목록 및 꽃말 |
| `flowers[].nameKo` | String | 꽃 이름(국문) |
| `flowers[].nameEn` | String | 꽃 이름(영문) |
| `flowers[].meaning` | String | 꽃말 |
| `createdAt` | String (ISO 8601) | 생성 일시 |

---

### DELETE `/analyses/:id` — 분석 결과 삭제

요청/응답 바디 없음. 성공 시 `204 No Content` 반환.

---

## 꽃다발 추천 (Recommendations)

### POST `/recommendations` — 상황 기반 꽃다발 추천

**Request Body**

| 항목 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `situation` | String | 필수 | 자연어로 입력한 상황 설명 |

**Response Body**

| 항목 | 타입 | 설명 |
| --- | --- | --- |
| `title` | String | 추천 제목 |
| `content` | String | 꽃다발 꽃말 조합 문장 |
| `flowers` | Array\<Object\> | 추천 꽃 목록 |
| `flowers[].nameKo` | String | 꽃 이름(국문) |
| `flowers[].nameEn` | String | 꽃 이름(영문) |
| `flowers[].meaning` | String | 꽃말 |

---

## 공통 에러 코드

| HTTP 상태코드 | 에러코드 | 설명 |
| --- | --- | --- |
| 400 | `INVALID_REQUEST` | 잘못된 요청 파라미터 |
| 401 | `UNAUTHORIZED` | 인증 토큰 없음 또는 만료 |
| 403 | `FORBIDDEN` | 접근 권한 없음 |
| 404 | `NOT_FOUND` | 리소스를 찾을 수 없음 |
| 409 | `CONFLICT` | 이미 존재하는 리소스 |
| 422 | `AI_PROCESSING_ERROR` | AI 모델 처리 실패 |
| 500 | `INTERNAL_SERVER_ERROR` | 서버 내부 오류 |
