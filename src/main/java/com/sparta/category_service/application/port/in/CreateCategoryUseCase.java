package com.sparta.category_service.application.port.in;

import com.sparta.category_service.application.port.in.dto.CategorySummaryDto;
import com.sparta.category_service.application.port.in.dto.CreateCategoryCommand;

// 카테고리 등록 UseCase
public interface CreateCategoryUseCase {

	// 카테고리를 등록하고 생성 결과를 반환한다
	CategorySummaryDto create(CreateCategoryCommand command);
}
