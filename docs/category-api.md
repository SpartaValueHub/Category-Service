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
