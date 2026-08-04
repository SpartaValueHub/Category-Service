package com.sparta.category_service.application.port.in;

import java.util.List;

import com.sparta.category_service.application.port.in.dto.CategorySummaryDto;

// 카테고리 자식(또는 최상위) 목록 조회 UseCase
public interface LoadCategoryChildrenUseCase {

	// parentUuid가 없으면 최상위, 있으면 해당 부모의 자식 목록을 조회한다
	List<CategorySummaryDto> loadChildren(String parentUuid, boolean includeInactive);
}
