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
	// 이 UUID 바로 뒤에 배치 (둘 중 하나만, 없으면 맨 뒤)
	private String insertAfterUuid;
	// 이 UUID 바로 앞에 배치 (둘 중 하나만, 없으면 맨 뒤)
	private String insertBeforeUuid;
}
