# Category API (FE 연동 스펙)

Category-Service 카테고리 API 명세서입니다.  
식별자는 내부 PK가 아니라 **`categoryUuid`** 를 사용합니다.

---

## 개요

| 항목 | 내용 |
|------|------|
| Base Path | `/api/v1/categories` |
| Auth | 현재 전 구간 `permitAll` (추후 BO 관리자 인증 예정) |
| Content-Type | 요청 Body가 있으면 `application/json` |
| 문자 인코딩 | UTF-8 |

### 도메인 개념

| 개념 | 설명 |
|------|------|
| 계층 | `parentUuid` / `depth`로 트리 구성. 최상위 `depth = 0` |
| sortOrder | **같은 부모 아래** 노출 순서 (작을수록 위). **활성만** 1..N 연속. 등록·수정 시 지정 번호는 **활성 기준** 자리. 생략 시 활성 마지막+1. 비활성은 항상 맨 뒤(먼저 비활성화한 것이 더 앞 번호). 생성·순서/부모 수정·비활성 후 구멍이 있으면 당겨서 재번호 |
| 리프(leaf) | 자식이 없는 끝 노드. 상품 등록 시 선택 대상 (브랜드 또는 브랜드 없는 중분류 끝단) |
| 소프트 삭제 | row 삭제 없음. `active=false` + `deleted_at` 설정. 비활성 시 해당 카테고리는 형제 중 맨 뒤 순번으로 이동하고, 나머지 활성은 앞으로 당겨 1..N 유지 |
| FO | 활성(`active=true`)만 사용. 카테고리 선택 **고정 없음** (등록 중 경로 변경 가능) |
| BO | 트리 관리용 등록·수정·삭제(비활성). `includeInactive`로 비활성 포함 조회 가능 |

### API 한눈에 보기

| Method | Path | 주 사용처 | 용도 |
|--------|------|-----------|------|
| `GET` | `/api/v1/categories/tree` | BO (FO 가능) | 전체 계층 트리 |
| `GET` | `/api/v1/categories` | FO / BO | 단계별 자식(또는 최상위) 목록 |
| `GET` | `/api/v1/categories/leaves` | FO | 상품 등록용 활성 리프 목록 |
| `POST` | `/api/v1/categories` | BO | 등록 |
| `PATCH` | `/api/v1/categories/{categoryUuid}` | BO | 부분 수정 |
| `DELETE` | `/api/v1/categories/{categoryUuid}` | BO | 소프트 삭제(비활성) |

### FE 화면 플로우 예시

**FO 상품 등록 (카테고리 선택, 고정 없음)**

1. `GET /categories` → 대분류  
2. `GET /categories?parentUuid={대분류}` → 중분류  
3. `GET /leaves?parentUuid={중분류}` → 리프(브랜드 등) 선택  
4. 대/중분류를 다시 바꿔도 됨 (요구사항: 선택 고정 없음)  
5. 최종 저장 값은 **선택한 리프의 `categoryUuid`** (Listing 쪽에서 보관)

또는 3번 대신 `GET /leaves`로 전체 활성 리프를 한 번에 받아 검색 UI를 구성할 수 있다.

**BO 카테고리 관리**

1. `GET /tree` 또는 `GET /categories`로 현황 확인  
2. `POST` 등록 / `PATCH` 수정 / `DELETE` 비활성  
3. 비활성 확인 시 `includeInactive=true`

### 공통 응답 타입

**요약 (Summary)** — 목록·단건·leaves

| 필드 | 타입 | 설명 |
|------|------|------|
| categoryUuid | string | 카테고리 UUID |
| categoryName | string | 카테고리명 |
| parentUuid | string \| null | 부모 UUID (최상위면 null) |
| sortOrder | number | 노출 순서 |
| depth | number | 계층 깊이 |
| active | boolean | 활성화 여부 |

**트리 노드** — Summary + `children` 배열(재귀)

### 공통 Error Response

```json
{
  "timestamp": "2026-08-05T12:00:00.000Z",
  "status": 404,
  "code": "CATEGORY_NOT_FOUND",
  "message": "카테고리를 찾을 수 없습니다.",
  "path": "/api/v1/categories/leaves"
}
```

| status | code | 의미 |
|--------|------|------|
| 400 | INVALID_ARGUMENT | 필수값·형식 오류 |
| 400 | INVALID_CATEGORY_HIERARCHY | 잘못된 부모 이동(순환 등) |
| 404 | CATEGORY_NOT_FOUND | 대상/부모 카테고리 없음 |
| 409 | DUPLICATE_CATEGORY_NAME | 같은 부모 아래 이름 중복 |
| 409 | CATEGORY_HAS_CHILDREN | 하위가 있어 삭제(비활성) 불가 |

조회 성공 시 결과가 없으면 **`200` + `[]`** (에러 아님).

---

## GET /api/v1/categories/tree

### Summary
카테고리 계층 트리를 조회한다. (주로 BO)

### Method · Path
`GET /api/v1/categories/tree`

### Auth
불필요 (현재 permitAll)

### Request

| 위치 | 필드 | 타입 | 필수 | 제약/설명 |
|------|------|------|------|-----------|
| Query | includeInactive | boolean | N | 기본 `false`. `true`면 비활성 포함 |

### Response
- Status: `200 OK`
- Body: 최상위 카테고리 배열 (자식은 `children`에 재귀)

| 필드 | 타입 | 설명 |
|------|------|------|
| categoryUuid | string | 카테고리 UUID |
| categoryName | string | 카테고리명 |
| parentUuid | string \| null | 부모 카테고리 UUID (최상위면 null) |
| sortOrder | number | 노출 순서 |
| depth | number | 계층 깊이 (최상위 0) |
| active | boolean | 활성화 여부 |
| children | array | 자식 카테고리 목록 |

```json
[
  {
    "categoryUuid": "11111111-1111-1111-1111-111111111111",
    "categoryName": "Luxury",
    "parentUuid": null,
    "sortOrder": 1,
    "depth": 0,
    "active": true,
    "children": [
      {
        "categoryUuid": "22222222-2222-2222-2222-222222222222",
        "categoryName": "가방",
        "parentUuid": "11111111-1111-1111-1111-111111111111",
        "sortOrder": 1,
        "depth": 1,
        "active": true,
        "children": []
      }
    ]
  }
]
```

### Errors
정상 목록만 반환한다. 데이터 없으면 `200` + `[]`.

---

## GET /api/v1/categories

### Summary
부모 UUID 기준 자식 카테고리 목록을 조회한다. `parentUuid`가 없으면 최상위 목록을 반환한다. (FO 단계 선택 / BO)

### Method · Path
`GET /api/v1/categories`

### Auth
불필요 (현재 permitAll)

### Request

| 위치 | 필드 | 타입 | 필수 | 제약/설명 |
|------|------|------|------|-----------|
| Query | parentUuid | string | N | 없으면 최상위(root) 목록 |
| Query | includeInactive | boolean | N | 기본 `false`. `true`면 비활성 포함 |

### Response
- Status: `200 OK`
- Body: 카테고리 요약 배열 (평탄 목록, children 없음)

```json
[
  {
    "categoryUuid": "22222222-2222-2222-2222-222222222222",
    "categoryName": "가방",
    "parentUuid": "11111111-1111-1111-1111-111111111111",
    "sortOrder": 1,
    "depth": 1,
    "active": true
  }
]
```

### Errors

| status | code | 의미 |
|--------|------|------|
| 404 | CATEGORY_NOT_FOUND | parentUuid에 해당하는 카테고리 없음 |

---

## GET /api/v1/categories/leaves

### Summary
FO 상품 등록용으로 **활성 리프(자식이 없는 끝 카테고리)** 목록을 조회한다.

### Method · Path
`GET /api/v1/categories/leaves`

### Auth
불필요 (현재 permitAll)

### Request

| 위치 | 필드 | 타입 | 필수 | 제약/설명 |
|------|------|------|------|-----------|
| Query | parentUuid | string | N | 없으면 전체 활성 리프. 있으면 해당 카테고리 하위 트리의 활성 리프만 |

- 비활성 카테고리는 제외한다
- 리프 = 활성 카테고리 중 자식이 없는 노드

### Response
- Status: `200 OK`
- Body: 카테고리 요약 배열 (`active`는 항상 `true`)

```json
[
  {
    "categoryUuid": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
    "categoryName": "샤넬",
    "parentUuid": "22222222-2222-2222-2222-222222222222",
    "sortOrder": 1,
    "depth": 2,
    "active": true
  }
]
```

### Errors

| status | code | 의미 |
|--------|------|------|
| 404 | CATEGORY_NOT_FOUND | parentUuid에 해당하는 활성 카테고리 없음 |

---

## POST /api/v1/categories

### Summary
카테고리를 등록한다. (총관리자 BO)

### Method · Path
`POST /api/v1/categories`

### Auth
불필요 (현재 permitAll)

### Request
- Content-Type: `application/json`

| 위치 | 필드 | 타입 | 필수 | 제약/설명 |
|------|------|------|------|-----------|
| Body | categoryName | string | Y | 최대 50자. 같은 부모 아래 중복 불가 |
| Body | parentUuid | string | N | 없으면 최상위(root). 있으면 해당 부모 하위 |
| Body | sortOrder | number | N | 없으면 **활성** 형제 마지막+1 (없으면 1). **지정하면** 활성만 기준 그 자리에 넣고 활성 1..N·비활성은 맨 뒤로 재번호 |

```json
{
  "categoryName": "시계",
  "parentUuid": "11111111-1111-1111-1111-111111111111",
  "sortOrder": 3
}
```

### Response
- Status: `201 Created`
- Body: 생성된 카테고리 단건 (요약)

### Errors

| status | code | 의미 |
|--------|------|------|
| 400 | INVALID_ARGUMENT | 카테고리명 누락/형식 오류, sortOrder 음수 등 |
| 404 | CATEGORY_NOT_FOUND | parentUuid에 해당하는 부모 없음 |
| 409 | DUPLICATE_CATEGORY_NAME | 같은 상위 아래 동일 카테고리명 존재 |

---

## PATCH /api/v1/categories/{categoryUuid}

### Summary
특정 카테고리를 부분 수정한다. (총관리자 BO)

### Method · Path
`PATCH /api/v1/categories/{categoryUuid}`

### Auth
불필요 (현재 permitAll)

### Request
- Content-Type: `application/json`
- Path의 `categoryUuid`에 해당하는 카테고리만 수정한다.
- Body에 **보낸 필드만** 반영한다. (안 보낸 필드는 유지)

| 위치 | 필드 | 타입 | 필수 | 제약/설명 |
|------|------|------|------|-----------|
| Path | categoryUuid | string | Y | 수정 대상 카테고리 UUID |
| Body | categoryName | string | N | 최대 50자. 같은 부모 아래 중복 불가 |
| Body | parentUuid | string \| null | N | 키를 보내지 않으면 부모 유지. `null`/빈값이면 최상위 이동. UUID면 해당 부모 하위로 이동 |
| Body | sortOrder | number | N | 없으면 기존 순서 유지(부모만 바뀌면 새 부모 **활성** 맨 뒤). **지정했고** 번호/부모가 바뀌면 새 부모의 **활성** 기준으로 그 자리에 배치 후 1..N 재번호 |

```json
{
  "categoryName": "시계/주얼리",
  "parentUuid": "11111111-1111-1111-1111-111111111111",
  "sortOrder": 2
}
```

최상위로 옮길 때:

```json
{
  "parentUuid": null
}
```

### Response
- Status: `200 OK`
- Body: 수정된 카테고리 단건 (요약)

### Errors

| status | code | 의미 |
|--------|------|------|
| 400 | INVALID_ARGUMENT | 카테고리명 형식 오류, sortOrder 음수 등 |
| 400 | INVALID_CATEGORY_HIERARCHY | 자기 자신/자기 하위로 부모 이동 시도 |
| 404 | CATEGORY_NOT_FOUND | 대상 또는 부모 카테고리 없음 |
| 409 | DUPLICATE_CATEGORY_NAME | 같은 상위 아래 동일 카테고리명 존재 |

---

## DELETE /api/v1/categories/{categoryUuid}

### Summary
카테고리를 소프트 삭제(비활성)한다. (총관리자 BO)

### Method · Path
`DELETE /api/v1/categories/{categoryUuid}`

### Auth
불필요 (현재 permitAll)

### Request

| 위치 | 필드 | 타입 | 필수 | 제약/설명 |
|------|------|------|------|-----------|
| Path | categoryUuid | string | Y | 비활성 대상 카테고리 UUID |

- Body 없음
- DB row는 유지하고 `active=false`, `deleted_at`을 채운다
- 하위 카테고리가 있으면 삭제할 수 없다
- 비활성된 카테고리는 같은 부모 형제 중 **맨 뒤** sortOrder로 이동하고, 남은 활성은 앞으로 당겨 1..N을 유지한다 (여러 비활성은 먼저 비활성화한 순으로 앞 번호)

### Response
- Status: `200 OK`
- Body: 비활성된 카테고리 단건 (요약, `active: false`)

### Errors

| status | code | 의미 |
|--------|------|------|
| 404 | CATEGORY_NOT_FOUND | 대상 카테고리 없음 |
| 409 | CATEGORY_HAS_CHILDREN | 하위 카테고리가 있어 삭제 불가 |
