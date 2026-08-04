package com.sparta.category_service.application.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.category_service.application.port.in.LoadCategoryTreeUseCase;
import com.sparta.category_service.application.port.in.dto.CategoryTreeNodeDto;
import com.sparta.category_service.application.port.out.CategoryLoadPort;
import com.sparta.category_service.domain.model.Category;

import lombok.RequiredArgsConstructor;

// 카테고리 조회 Application Service
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService implements LoadCategoryTreeUseCase {

	// 카테고리 조회 Port
	private final CategoryLoadPort categoryLoadPort;

	// 카테고리 계층 트리를 조회한다
	@Override
	public List<CategoryTreeNodeDto> loadTree(boolean includeInactive) {
		List<Category> categories = categoryLoadPort.findAll(includeInactive);
		return buildTree(categories);
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
			String parentUuid = category.getParentId() == null
					? null
					: uuidById.get(category.getParentId());

			nodeById.put(
					category.getCategoryId(),
					CategoryTreeNodeDto.builder()
							.categoryUuid(category.getCategoryUuid())
							.categoryName(category.getCategoryName())
							.parentUuid(parentUuid)
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
