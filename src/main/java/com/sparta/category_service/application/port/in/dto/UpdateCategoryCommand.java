package com.sparta.category_service.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

// 카테고리 수정 요청 Command (보낸 필드만 반영)
@Getter
@Builder
public class UpdateCategoryCommand {

	// 수정 대상 카테고리 UUID
	private final String categoryUuid;
	// 카테고리명 (null이면 유지)
	private final String categoryName;
	// 부모 UUID 변경 요청 여부 (JSON에 parentUuid 키가 있었는지)
	private final boolean parentUuidSpecified;
	// 부모 UUID (지정됐고 null/blank면 최상위)
	private final String parentUuid;
	// insertAfterUuid 키 포함 여부
	private final boolean insertAfterUuidSpecified;
	// insertBeforeUuid 키 포함 여부
	private final boolean insertBeforeUuidSpecified;
	// 이 UUID 바로 뒤에 배치
	private final String insertAfterUuid;
	// 이 UUID 바로 앞에 배치
	private final String insertBeforeUuid;
}
