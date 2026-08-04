package com.sparta.category_service.application.port.in;

import java.util.List;

import com.sparta.category_service.application.port.in.dto.CategoryTreeNodeDto;

// 카테고리 트리 조회 UseCase
public interface LoadCategoryTreeUseCase {

	// 카테고리 계층 트리를 조회한다
	List<CategoryTreeNodeDto> loadTree(boolean includeInactive);
}
