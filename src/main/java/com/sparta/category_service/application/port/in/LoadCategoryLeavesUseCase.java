package com.sparta.category_service.application.port.in;

import java.util.List;

import com.sparta.category_service.application.port.in.dto.CategorySummaryDto;

// FO 활성 리프 카테고리 조회 UseCase
public interface LoadCategoryLeavesUseCase {

	// 활성 리프 목록을 조회한다 (parentUuid가 있으면 그 하위 트리만)
	List<CategorySummaryDto> loadLeaves(String parentUuid);
}
