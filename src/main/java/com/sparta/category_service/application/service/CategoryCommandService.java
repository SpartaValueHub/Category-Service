package com.sparta.category_service.application.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.category_service.application.port.in.CreateCategoryUseCase;
import com.sparta.category_service.application.port.in.dto.CategorySummaryDto;
import com.sparta.category_service.application.port.in.dto.CreateCategoryCommand;
import com.sparta.category_service.application.port.out.CategoryLoadPort;
import com.sparta.category_service.application.port.out.CategorySavePort;
import com.sparta.category_service.domain.exception.CategoryNotFoundException;
import com.sparta.category_service.domain.exception.DuplicateCategoryNameException;
import com.sparta.category_service.domain.model.Category;

import lombok.RequiredArgsConstructor;

// 카테고리 변경 Application Service
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryCommandService implements CreateCategoryUseCase {

	// 카테고리 조회 Port
	private final CategoryLoadPort categoryLoadPort;
	// 카테고리 저장 Port
	private final CategorySavePort categorySavePort;

	// 카테고리를 등록한다 (parentUuid 없으면 최상위)
	@Override
	public CategorySummaryDto create(CreateCategoryCommand command) {
		String categoryName = requireName(command.getCategoryName());

		Long parentId = null;
		int depth = 0;
		String parentUuid = null;

		if (command.getParentUuid() != null && !command.getParentUuid().isBlank()) {
			Category parent = categoryLoadPort.findByUuid(command.getParentUuid().trim())
					.orElseThrow(() -> new CategoryNotFoundException("부모 카테고리를 찾을 수 없습니다."));
			parentId = parent.getCategoryId();
			depth = parent.getDepth() + 1;
			parentUuid = parent.getCategoryUuid();
		}

		if (categoryLoadPort.existsByParentIdAndName(parentId, categoryName)) {
			throw new DuplicateCategoryNameException("같은 상위 아래에 동일한 카테고리명이 이미 있습니다.");
		}

		int sortOrder = resolveSortOrder(parentId, command.getSortOrder());
		Category created = Category.create(
				UUID.randomUUID().toString(),
				categoryName,
				parentId,
				depth,
				sortOrder,
				Instant.now()
		);
		Category saved = categorySavePort.save(created);

		return CategorySummaryDto.builder()
				.categoryUuid(saved.getCategoryUuid())
				.categoryName(saved.getCategoryName())
				.parentUuid(parentUuid)
				.sortOrder(saved.getSortOrder())
				.depth(saved.getDepth())
				.active(saved.isActive())
				.build();
	}

	// 카테고리명 필수·trim
	private String requireName(String categoryName) {
		if (categoryName == null || categoryName.isBlank()) {
			throw new IllegalArgumentException("카테고리명은 필수입니다.");
		}
		return categoryName.trim();
	}

	// sortOrder 미입력이면 같은 부모 마지막+1 (형제 없으면 1)
	private int resolveSortOrder(Long parentId, Integer requestedSortOrder) {
		if (requestedSortOrder != null) {
			return requestedSortOrder;
		}

		List<Category> siblings = parentId == null
				? categoryLoadPort.findRoots(true)
				: categoryLoadPort.findChildren(parentId, true);

		int maxSortOrder = siblings.stream()
				.mapToInt(Category::getSortOrder)
				.max()
				.orElse(0);
		return maxSortOrder + 1;
	}
}
