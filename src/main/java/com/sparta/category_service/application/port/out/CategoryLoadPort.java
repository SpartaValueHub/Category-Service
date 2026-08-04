package com.sparta.category_service.application.port.out;

import java.util.List;
import java.util.Optional;

import com.sparta.category_service.domain.model.Category;

// 카테고리 조회 Output Port
public interface CategoryLoadPort {

	// 카테고리 목록 조회 (includeInactive=false면 활성만)
	List<Category> findAll(boolean includeInactive);

	// 카테고리 UUID로 단건 조회
	Optional<Category> findByUuid(String categoryUuid);

	// 최상위 카테고리 목록 조회
	List<Category> findRoots(boolean includeInactive);

	// 특정 부모의 자식 카테고리 목록 조회
	List<Category> findChildren(Long parentId, boolean includeInactive);

	// 같은 부모 아래 동일 카테고리명 존재 여부 (parentId null이면 최상위)
	boolean existsByParentIdAndName(Long parentId, String categoryName);
}
