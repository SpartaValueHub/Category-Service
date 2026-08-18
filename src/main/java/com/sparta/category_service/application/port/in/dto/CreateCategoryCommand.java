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
	// 이 UUID 바로 뒤에 배치 (둘 중 하나만, 없으면 맨 뒤)
	private final String insertAfterUuid;
	// 이 UUID 바로 앞에 배치 (둘 중 하나만, 없으면 맨 뒤)
	private final String insertBeforeUuid;
}
