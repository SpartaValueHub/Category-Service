package com.sparta.category_service.application.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.category_service.application.port.in.LoadCategoryChildrenUseCase;
import com.sparta.category_service.application.port.in.LoadCategoryTreeUseCase;
import com.sparta.category_service.application.port.in.dto.CategorySummaryDto;
import com.sparta.category_service.application.port.in.dto.CategoryTreeNodeDto;
import com.sparta.category_service.application.port.out.CategoryLoadPort;
import com.sparta.category_service.domain.exception.CategoryNotFoundException;
import com.sparta.category_service.domain.model.Category;

import lombok.RequiredArgsConstructor;

// 카테고리 조회 Application Service
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService implements LoadCategoryTreeUseCase, LoadCategoryChildrenUseCase {

	// 카테고리 조회 Port
	private final CategoryLoadPort categoryLoadPort;

	// 카테고리 계층 트리를 조회한다
	@Override
	public List<CategoryTreeNodeDto> loadTree(boolean includeInactive) {
		List<Category> categories = categoryLoadPort.findAll(includeInactive);
		return buildTree(categories);
	}

	// parentUuid가 없으면 최상위, 있으면 해당 부모의 자식 목록을 조회한다
	@Override
	public List<CategorySummaryDto> loadChildren(String parentUuid, boolean includeInactive) {
		if (parentUuid == null || parentUuid.isBlank()) {
			return toSummaries(categoryLoadPort.findRoots(includeInactive), null);
		}

		Category parent = categoryLoadPort.findByUuid(parentUuid.trim())
				.orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

		return toSummaries(
				categoryLoadPort.findChildren(parent.getCategoryId(), includeInactive),
				parent.getCategoryUuid()
		);
	}

	// Domain 목록을 요약 DTO로 변환한다
	private List<CategorySummaryDto> toSummaries(List<Category> categories, String parentUuid) {
		return categories.stream()
				.map(category -> CategorySummaryDto.builder()
						.categoryUuid(category.getCategoryUuid())
						.categoryName(category.getCategoryName())
						.parentUuid(parentUuid)
						.sortOrder(category.getSortOrder())
						.depth(category.getDepth())
						.active(category.isActive())
						.build())
				.toList();
	}

	// 평탄 목록을 부모-자식 트리로 변환한다
	private List<CategoryTreeNodeDto> buildTree(List<Category> categories) {
		// categoryId -> UUID
		Map<Long, String> uuidById = new LinkedHashMap<>();
		for (Category category : categories) {
			uuidById.put(category.getCategoryId(), category.getCategoryUuid());
		}

		List<Category> sorted = categories.stream()
				.sorted(Comparator
						.comparingInt(Category::getSortOrder)
						.thenComparing(Category::getCategoryId))
				.toList();

		// categoryId -> 트리 노드
		Map<Long, CategoryTreeNodeDto> nodeById = new LinkedHashMap<>();
		for (Category category : sorted) {
			String mappedParentUuid = category.getParentId() == null
					? null
					: uuidById.get(category.getParentId());

			nodeById.put(
					category.getCategoryId(),
					CategoryTreeNodeDto.builder()
							.categoryUuid(category.getCategoryUuid())
							.categoryName(category.getCategoryName())
							.parentUuid(mappedParentUuid)
							.sortOrder(category.getSortOrder())
							.depth(category.getDepth())
							.active(category.isActive())
							.children(new ArrayList<>())
							.build()
			);
		}

		// 최상위 노드 목록
		List<CategoryTreeNodeDto> roots = new ArrayList<>();
		for (Category category : sorted) {
			CategoryTreeNodeDto node = nodeById.get(category.getCategoryId());
			if (category.getParentId() == null) {
				roots.add(node);
				continue;
			}

			CategoryTreeNodeDto parentNode = nodeById.get(category.getParentId());
			if (parentNode != null) {
				parentNode.getChildren().add(node);
			}
		}

		return roots;
	}
}
