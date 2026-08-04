package com.sparta.category_service.adaptor.in.web.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 카테고리 등록 요청 VO
@Getter
@NoArgsConstructor
public class CreateCategoryRequestVo {

	// 카테고리명
	private String categoryName;
	// 부모 카테고리 UUID (없으면 최상위)
	private String parentUuid;
	// 노출 순서 (없으면 같은 부모 마지막+1)
	private Integer sortOrder;
}
