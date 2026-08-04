package com.sparta.category_service.application.port.out;

import com.sparta.category_service.domain.model.Category;

// 카테고리 저장 Output Port
public interface CategorySavePort {

	// 카테고리 신규 저장 (저장 후 ID가 채워진 Domain 반환)
	Category save(Category category);
}
