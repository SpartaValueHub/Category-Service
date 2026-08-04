package com.sparta.category_service.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

// 카테고리 요약 (평탄 목록 응답)
@Getter
@Builder
public class CategorySummaryDto {

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
