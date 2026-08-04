package com.sparta.category_service.adaptor.in.web.vo;

import lombok.Builder;
import lombok.Getter;

// 카테고리 요약 응답 VO
@Getter
@Builder
public class CategorySummaryResponseVo {

	// 카테고리 UUID
	private final String categoryUuid;
	// 카테고리명
	private final String categoryName;
	// 부모 카테고리 UUID (최상위면 null)
	private final String parentUuid;
	// 노출 순서
	private final int sortOrder;
	// 깊이
	private final int depth;
	// 활성화 여부
	private final boolean active;
}
