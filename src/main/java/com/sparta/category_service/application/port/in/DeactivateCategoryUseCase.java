package com.sparta.category_service.application.port.in;

import com.sparta.category_service.application.port.in.dto.CategorySummaryDto;

// 카테고리 삭제(비활성) UseCase
public interface DeactivateCategoryUseCase {

	// 카테고리를 소프트 삭제(비활성)하고 결과를 반환한다
	CategorySummaryDto deactivate(String categoryUuid);
}
