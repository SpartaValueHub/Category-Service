package com.sparta.category_service.application.port.in.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

// 카테고리 트리 노드 (Application 응답)
@Getter
@Builder
public class CategoryTreeNodeDto {

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
	// 자식 카테고리 목록
	private final List<CategoryTreeNodeDto> children;
}
