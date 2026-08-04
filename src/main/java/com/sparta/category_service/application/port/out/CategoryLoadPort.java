package com.sparta.category_service.application.port.out;

import java.util.List;

import com.sparta.category_service.domain.model.Category;

// 카테고리 조회 Output Port
public interface CategoryLoadPort {

	// 카테고리 목록 조회 (includeInactive=false면 활성만)
	List<Category> findAll(boolean includeInactive);
}
