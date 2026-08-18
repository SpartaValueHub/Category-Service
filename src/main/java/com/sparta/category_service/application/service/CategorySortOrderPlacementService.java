package com.sparta.category_service.application.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.sparta.category_service.application.port.out.CategoryLoadPort;
import com.sparta.category_service.application.port.out.CategorySavePort;
import com.sparta.category_service.domain.exception.CategoryNotFoundException;
import com.sparta.category_service.domain.model.Category;
import com.sparta.category_service.domain.policy.CategorySortOrderPolicy;
import com.sparta.category_service.domain.policy.CategorySortOrderPolicy.PlacementResult;
import com.sparta.category_service.domain.policy.CategorySortOrderPolicy.SiblingSortOrderUpdate;

import lombok.RequiredArgsConstructor;

// 같은 부모 형제 기준 sort_order gap 배치
@Component
@RequiredArgsConstructor
public class CategorySortOrderPlacementService {

	// 카테고리 조회 Port
	private final CategoryLoadPort categoryLoadPort;
	// 카테고리 저장 Port
	private final CategorySavePort categorySavePort;

	// 활성 형제 기준 삽입 위치에 sort_order를 계산하고 필요 시 rebalance 저장
	public int placeAndPersist(
			Long parentId,
			String insertAfterUuid,
			String insertBeforeUuid,
			Long excludeCategoryId
	) {
		validatePlacementRequest(insertAfterUuid, insertBeforeUuid);

		List<Category> activeSiblings = loadActiveSiblingsSorted(parentId, excludeCategoryId);
		int insertIndex = resolveInsertIndex(activeSiblings, insertAfterUuid, insertBeforeUuid);

		List<Integer> siblingSortOrders = activeSiblings.stream()
				.map(Category::getSortOrder)
				.toList();

		PlacementResult placement = CategorySortOrderPolicy.place(siblingSortOrders, insertIndex);
		applySiblingUpdates(activeSiblings, placement.siblingUpdates());

		return placement.insertedSortOrder();
	}

	private void validatePlacementRequest(String insertAfterUuid, String insertBeforeUuid) {
		boolean hasAfter = insertAfterUuid != null && !insertAfterUuid.isBlank();
		boolean hasBefore = insertBeforeUuid != null && !insertBeforeUuid.isBlank();
		if (hasAfter && hasBefore) {
			throw new IllegalArgumentException("insertAfterUuid와 insertBeforeUuid는 동시에 보낼 수 없습니다.");
		}
	}

	private int resolveInsertIndex(
			List<Category> activeSiblings,
			String insertAfterUuid,
			String insertBeforeUuid
	) {
		if (insertAfterUuid != null && !insertAfterUuid.isBlank()) {
			int anchorIndex = findSiblingIndex(activeSiblings, insertAfterUuid.trim());
			return anchorIndex + 1;
		}
		if (insertBeforeUuid != null && !insertBeforeUuid.isBlank()) {
			return findSiblingIndex(activeSiblings, insertBeforeUuid.trim());
		}
		return activeSiblings.size();
	}

	private int findSiblingIndex(List<Category> activeSiblings, String anchorUuid) {
		for (int index = 0; index < activeSiblings.size(); index++) {
			if (activeSiblings.get(index).getCategoryUuid().equals(anchorUuid)) {
				return index;
			}
		}
		throw new CategoryNotFoundException("순서 기준 카테고리를 찾을 수 없습니다.");
	}

	private void applySiblingUpdates(List<Category> activeSiblings, List<SiblingSortOrderUpdate> siblingUpdates) {
		for (SiblingSortOrderUpdate update : siblingUpdates) {
			Category sibling = activeSiblings.get(update.siblingIndex());
			sibling.changeSortOrder(update.newSortOrder());
			categorySavePort.update(sibling);
		}
	}

	// 활성 형제 (sort_order 오름차순)
	private List<Category> loadActiveSiblingsSorted(Long parentId, Long excludeCategoryId) {
		List<Category> siblings = parentId == null
				? categoryLoadPort.findRoots(false)
				: categoryLoadPort.findChildren(parentId, false);

		return siblings.stream()
				.filter(Category::isActive)
				.filter(sibling -> excludeCategoryId == null
						|| !Objects.equals(sibling.getCategoryId(), excludeCategoryId))
				.sorted(Comparator.comparingInt(Category::getSortOrder)
						.thenComparing(Category::getCategoryId))
				.toList();
	}
}
