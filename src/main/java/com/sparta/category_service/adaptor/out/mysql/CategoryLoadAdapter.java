package com.sparta.category_service.adaptor.out.mysql;

import java.util.List;
import java.util.Optional;

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

	// 카테고리 UUID로 단건 조회
	@Override
	public Optional<Category> findByUuid(String categoryUuid) {
		return categoryJpaRepository.findByCategoryUuid(categoryUuid)
				.map(CategoryEntityMapper::toDomain);
	}

	// 최상위 카테고리 목록 조회
	@Override
	public List<Category> findRoots(boolean includeInactive) {
		List<CategoryEntity> entities = includeInactive
				? categoryJpaRepository.findByParentIdIsNullOrderBySortOrderAscCategoryIdAsc()
				: categoryJpaRepository.findByParentIdIsNullAndActiveTrueOrderBySortOrderAscCategoryIdAsc();

		return entities.stream()
				.map(CategoryEntityMapper::toDomain)
				.toList();
	}

	// 특정 부모의 자식 카테고리 목록 조회
	@Override
	public List<Category> findChildren(Long parentId, boolean includeInactive) {
		List<CategoryEntity> entities = includeInactive
				? categoryJpaRepository.findByParentIdOrderBySortOrderAscCategoryIdAsc(parentId)
				: categoryJpaRepository.findByParentIdAndActiveTrueOrderBySortOrderAscCategoryIdAsc(parentId);

		return entities.stream()
				.map(CategoryEntityMapper::toDomain)
				.toList();
	}

	// 같은 부모 아래 동일 카테고리명 존재 여부
	@Override
	public boolean existsByParentIdAndName(Long parentId, String categoryName) {
		if (parentId == null) {
			return categoryJpaRepository.existsByParentIdIsNullAndCategoryName(categoryName);
		}
		return categoryJpaRepository.existsByParentIdAndCategoryName(parentId, categoryName);
	}
}
