package com.sparta.category_service.adaptor.in.web.vo;

import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 카테고리 수정 요청 VO (보낸 필드만 반영)
@Getter
@NoArgsConstructor
public class UpdateCategoryRequestVo {

	// 카테고리명 (없으면 유지)
	private String categoryName;
	// 노출 순서 (없으면 유지)
	private Integer sortOrder;
	// 부모 카테고리 UUID (키를 보냈고 null/blank면 최상위)
	private String parentUuid;
	// JSON에 parentUuid 키가 포함되었는지
	private boolean parentUuidSpecified;

	// parentUuid 키가 요청에 있을 때만 호출된다
	@JsonSetter("parentUuid")
	public void setParentUuid(String parentUuid) {
		this.parentUuidSpecified = true;
		this.parentUuid = parentUuid;
	}
}
