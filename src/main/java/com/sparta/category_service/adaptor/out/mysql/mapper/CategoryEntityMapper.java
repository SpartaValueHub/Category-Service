package com.sparta.category_service.adaptor.out.mysql.mapper;

import com.sparta.category_service.adaptor.out.mysql.entity.CategoryEntity;
import com.sparta.category_service.domain.model.Category;

// Category Entity <-> Domain 변환
public final class CategoryEntityMapper {

	private CategoryEntityMapper() {
	}

	// Entity를 Domain으로 복원한다
	public static Category toDomain(CategoryEntity entity) {
		return Category.restore(
				entity.getCategoryId(),
				entity.getCategoryUuid(),
				entity.getParentId(),
				entity.getCategoryName(),
				entity.getSortOrder(),
				entity.getDepth(),
				entity.isActive(),
				entity.getCreatedAt(),
				entity.getDeletedAt()
		);
	}
}
