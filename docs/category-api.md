# Category API

## GET /api/v1/categories/tree

### Summary
카테고리 계층 트리를 조회한다.

### Method · Path
`GET /api/v1/categories/tree`

### Auth
불필요 (현재 permitAll)

### Request

| 위치 | 필드 | 타입 | 필수 | 제약/설명 |
|------|------|------|------|-----------|
| Query | includeInactive | boolean | N | 기본 `false`. `true`면 비활성 카테고리 포함 |

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
공통 Error Response 형식은 후속 예외 고도화 PR에서 확장한다. 현재 트리 조회는 정상 목록(빈 배열 포함)을 반환한다.

---

## GET /api/v1/categories

### Summary
부모 UUID 기준 자식 카테고리 목록을 조회한다. `parentUuid`가 없으면 최상위 목록을 반환한다.

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

| 필드 | 타입 | 설명 |
|------|------|------|
| categoryUuid | string | 카테고리 UUID |
| categoryName | string | 카테고리명 |
| parentUuid | string \| null | 부모 카테고리 UUID |
| sortOrder | number | 노출 순서 |
| depth | number | 계층 깊이 |
| active | boolean | 활성화 여부 |

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
| Body | sortOrder | number | N | 없으면 같은 부모 형제 중 마지막+1 (형제 없으면 1) |

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

| 필드 | 타입 | 설명 |
|------|------|------|
| categoryUuid | string | 카테고리 UUID |
| categoryName | string | 카테고리명 |
| parentUuid | string \| null | 부모 카테고리 UUID |
| sortOrder | number | 노출 순서 |
| depth | number | 계층 깊이 (부모 depth+1, 최상위 0) |
| active | boolean | 활성화 여부 (생성 시 true) |

```json
{
  "categoryUuid": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
  "categoryName": "시계",
  "parentUuid": "11111111-1111-1111-1111-111111111111",
  "sortOrder": 3,
  "depth": 1,
  "active": true
}
```

### Errors

| status | code | 의미 |
|--------|------|------|
| 400 | INVALID_ARGUMENT | 카테고리명 누락/형식 오류, sortOrder 음수 등 |
| 404 | CATEGORY_NOT_FOUND | parentUuid에 해당하는 부모 없음 |
| 409 | DUPLICATE_CATEGORY_NAME | 같은 상위 아래 동일 카테고리명 존재 |

