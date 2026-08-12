package com.sparta.category_service.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

// 카테고리 등록 요청 Command
@Getter
@Builder
public class CreateCategoryCommand {

	// 카테고리명
	private final String categoryName;
	// 부모 카테고리 UUID (없으면 최상위)
	private final String parentUuid;
	// 노출 순서 (없으면 같은 부모 마지막+1)
	private final Integer sortOrder;
}
