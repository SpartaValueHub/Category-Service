package com.sparta.category_service.application.port.in;

import com.sparta.category_service.application.port.in.dto.CategorySummaryDto;
import com.sparta.category_service.application.port.in.dto.UpdateCategoryCommand;

// 카테고리 수정 UseCase
public interface UpdateCategoryUseCase {

	// 카테고리를 부분 수정하고 결과를 반환한다
	CategorySummaryDto update(UpdateCategoryCommand command);
}
