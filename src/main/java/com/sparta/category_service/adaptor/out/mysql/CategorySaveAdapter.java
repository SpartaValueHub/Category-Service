package com.sparta.category_service.adaptor.out.mysql;

import org.springframework.stereotype.Component;

import com.sparta.category_service.adaptor.out.mysql.entity.CategoryEntity;
import com.sparta.category_service.adaptor.out.mysql.mapper.CategoryEntityMapper;
import com.sparta.category_service.adaptor.out.mysql.repository.CategoryJpaRepository;
import com.sparta.category_service.application.port.out.CategorySavePort;
import com.sparta.category_service.domain.model.Category;

import lombok.RequiredArgsConstructor;

// 카테고리 저장 Adapter
@Component
@RequiredArgsConstructor
public class CategorySaveAdapter implements CategorySavePort {

	// 카테고리 JPA Repository
	private final CategoryJpaRepository categoryJpaRepository;

	// 카테고리 신규 저장
	@Override
	public Category save(Category category) {
		CategoryEntity entity = CategoryEntity.create(
				category.getCategoryUuid(),
				category.getParentId(),
				category.getCategoryName(),
				category.getSortOrder(),
				category.getDepth(),
				category.isActive(),
				category.getCreatedAt()
		);
		CategoryEntity saved = categoryJpaRepository.save(entity);
		return CategoryEntityMapper.toDomain(saved);
	}

	// 기존 카테고리 변경 저장
	@Override
	public Category update(Category category) {
		CategoryEntity entity = categoryJpaRepository.findById(category.getCategoryId())
				.orElseThrow(() -> new IllegalArgumentException("수정할 카테고리를 찾을 수 없습니다."));
		entity.update(
				category.getParentId(),
				category.getCategoryName(),
				category.getSortOrder(),
				category.getDepth(),
				category.isActive(),
				category.getDeletedAt()
		);
		return CategoryEntityMapper.toDomain(entity);
	}
}
