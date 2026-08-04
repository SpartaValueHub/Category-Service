package com.sparta.category_service.adaptor.out.mysql;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sparta.category_service.adaptor.out.mysql.entity.CategoryEntity;
import com.sparta.category_service.adaptor.out.mysql.mapper.CategoryEntityMapper;
import com.sparta.category_service.adaptor.out.mysql.repository.CategoryJpaRepository;
import com.sparta.category_service.application.port.out.CategoryLoadPort;
import com.sparta.category_service.domain.model.Category;

import lombok.RequiredArgsConstructor;

// 카테고리 조회 Adapter
@Component
@RequiredArgsConstructor
public class CategoryLoadAdapter implements CategoryLoadPort {

	// 카테고리 JPA Repository
	private final CategoryJpaRepository categoryJpaRepository;

	// 카테고리 목록 조회 (includeInactive=false면 활성만)
	@Override
	public List<Category> findAll(boolean includeInactive) {
		List<CategoryEntity> entities = includeInactive
				? categoryJpaRepository.findAllByOrderBySortOrderAscCategoryIdAsc()
				: categoryJpaRepository.findByActiveTrueOrderBySortOrderAscCategoryIdAsc();

		return entities.stream()
				.map(CategoryEntityMapper::toDomain)
				.toList();
	}
}
