package com.sparta.category_service.application.service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.category_service.application.port.in.CreateCategoryUseCase;
import com.sparta.category_service.application.port.in.DeactivateCategoryUseCase;
import com.sparta.category_service.application.port.in.UpdateCategoryUseCase;
import com.sparta.category_service.application.port.in.dto.CategorySummaryDto;
import com.sparta.category_service.application.port.in.dto.CreateCategoryCommand;
import com.sparta.category_service.application.port.in.dto.UpdateCategoryCommand;
import com.sparta.category_service.application.port.out.CategoryLoadPort;
import com.sparta.category_service.application.port.out.CategorySavePort;
import com.sparta.category_service.domain.exception.CategoryHasChildrenException;
import com.sparta.category_service.domain.exception.CategoryNotFoundException;
import com.sparta.category_service.domain.exception.DuplicateCategoryNameException;
import com.sparta.category_service.domain.exception.InvalidCategoryHierarchyException;
import com.sparta.category_service.domain.model.Category;

import lombok.RequiredArgsConstructor;

// 카테고리 변경 Application Service
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryCommandService implements CreateCategoryUseCase, UpdateCategoryUseCase, DeactivateCategoryUseCase {

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

		// 활성 형제만 기준으로 자리 결정. 생략하면 활성 마지막+1
		List<Category> activeSiblings = loadActiveSiblingsSorted(parentId, null);
		int position = resolveActivePosition(command.getSortOrder(), activeSiblings.size());

		Category created = Category.create(
				UUID.randomUUID().toString(),
				categoryName,
				parentId,
				depth,
				position,
				Instant.now()
		);
		Category saved = categorySavePort.save(created);

		List<Category> orderedActives = new ArrayList<>(activeSiblings);
		orderedActives.add(position - 1, saved);
		persistSiblingOrders(orderedActives, loadInactiveSiblingsSorted(parentId));

		return toSummary(saved, parentUuid);
	}

	// 카테고리를 부분 수정한다
	@Override
	public CategorySummaryDto update(UpdateCategoryCommand command) {
		if (command.getCategoryUuid() == null || command.getCategoryUuid().isBlank()) {
			throw new IllegalArgumentException("카테고리 UUID는 필수입니다.");
		}

		Category target = categoryLoadPort.findByUuid(command.getCategoryUuid().trim())
				.orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

		Long previousParentId = target.getParentId();
		// 변경 후 이름·부모·깊이·순서
		String nextName = target.getCategoryName();
		Long nextParentId = target.getParentId();
		int nextDepth = target.getDepth();
		Integer requestedSortOrder = command.getSortOrder();
		// 부모 변경으로 depth 재계산이 필요한지
		boolean hierarchyChanged = false;

		if (command.getCategoryName() != null) {
			nextName = requireName(command.getCategoryName());
		}

		if (command.isParentUuidSpecified()) {
			ParentResolution parentResolution = resolveParentForUpdate(target, command.getParentUuid());
			if (!Objects.equals(nextParentId, parentResolution.parentId())) {
				hierarchyChanged = true;
			}
			nextParentId = parentResolution.parentId();
			nextDepth = parentResolution.depth();
		}

		boolean nameChanged = !nextName.equals(target.getCategoryName());
		boolean parentChanged = !Objects.equals(nextParentId, target.getParentId());
		boolean sortOrderSpecified = requestedSortOrder != null;
		boolean sortOrderChanged = sortOrderSpecified && requestedSortOrder != target.getSortOrder();
		if (nameChanged || parentChanged) {
			if (categoryLoadPort.existsByParentIdAndNameExcludingId(
					nextParentId,
					nextName,
					target.getCategoryId()
			)) {
				throw new DuplicateCategoryNameException("같은 상위 아래에 동일한 카테고리명이 이미 있습니다.");
			}
		}

		int previousDepth = target.getDepth();
		if (nameChanged) {
			target.rename(nextName);
		}
		if (hierarchyChanged || parentChanged) {
			target.changeHierarchy(nextParentId, nextDepth);
		}

		Category saved = categorySavePort.update(target);

		// 부모 이동으로 depth가 바뀌면 하위 노드 depth도 같이 맞춤
		int depthDelta = saved.getDepth() - previousDepth;
		if (depthDelta != 0) {
			updateDescendantDepths(saved.getCategoryId(), depthDelta);
		}

		// 활성 카테고리의 부모·순서가 바뀌면 활성 기준으로 재배치 후 1..N 재번호
		boolean orderAffecting = saved.isActive() && (parentChanged || sortOrderChanged);
		if (orderAffecting) {
			List<Category> activeSiblings = loadActiveSiblingsSorted(nextParentId, saved.getCategoryId());
			int position = sortOrderSpecified
					? resolveActivePosition(requestedSortOrder, activeSiblings.size())
					: activeSiblings.size() + 1;
			List<Category> orderedActives = new ArrayList<>(activeSiblings);
			orderedActives.add(position - 1, saved);
			persistSiblingOrders(orderedActives, loadInactiveSiblingsSorted(nextParentId));
		}

		// 부모를 떠났으면 이전 부모 재번호. 활성 재배치를 안 탄 경우(비활성 이동 등)는 새 부모도 재번호
		if (parentChanged) {
			compactSiblings(previousParentId);
			if (!orderAffecting) {
				compactSiblings(nextParentId);
			}
		}

		Category refreshed = categoryLoadPort.findByUuid(saved.getCategoryUuid()).orElse(saved);
		String parentUuid = resolveParentUuid(refreshed.getParentId());
		return toSummary(refreshed, parentUuid);
	}

	// 카테고리를 소프트 삭제(비활성)한다
	@Override
	public CategorySummaryDto deactivate(String categoryUuid) {
		if (categoryUuid == null || categoryUuid.isBlank()) {
			throw new IllegalArgumentException("카테고리 UUID는 필수입니다.");
		}

		Category target = categoryLoadPort.findByUuid(categoryUuid.trim())
				.orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

		if (categoryLoadPort.existsChildren(target.getCategoryId())) {
			throw new CategoryHasChildrenException("하위 카테고리가 있어 삭제할 수 없습니다.");
		}

		Long parentId = target.getParentId();
		target.deactivate(Instant.now());
		Category saved = categorySavePort.update(target);

		// 활성은 앞으로 당기고, 비활성은 비활성 시각 순으로 맨 뒤
		compactSiblings(parentId);

		Category refreshed = categoryLoadPort.findByUuid(saved.getCategoryUuid()).orElse(saved);
		String parentUuid = resolveParentUuid(refreshed.getParentId());
		return toSummary(refreshed, parentUuid);
	}

	// 수정 시 부모 UUID를 parentId·depth로 해석한다
	private ParentResolution resolveParentForUpdate(Category target, String parentUuid) {
		if (parentUuid == null || parentUuid.isBlank()) {
			return new ParentResolution(null, 0);
		}

		Category parent = categoryLoadPort.findByUuid(parentUuid.trim())
				.orElseThrow(() -> new CategoryNotFoundException("부모 카테고리를 찾을 수 없습니다."));

		if (parent.getCategoryId().equals(target.getCategoryId())) {
			throw new InvalidCategoryHierarchyException("자기 자신을 부모로 지정할 수 없습니다.");
		}
		assertNotMovingUnderDescendant(target.getCategoryId(), parent);

		return new ParentResolution(parent.getCategoryId(), parent.getDepth() + 1);
	}

	// 새 부모가 자신의 하위면 순환이므로 막는다
	private void assertNotMovingUnderDescendant(Long targetId, Category newParent) {
		Category cursor = newParent;
		while (cursor.getParentId() != null) {
			if (cursor.getParentId().equals(targetId)) {
				throw new InvalidCategoryHierarchyException("자신의 하위 카테고리로는 이동할 수 없습니다.");
			}
			Long parentId = cursor.getParentId();
			cursor = categoryLoadPort.findById(parentId)
					.orElseThrow(() -> new CategoryNotFoundException("부모 카테고리를 찾을 수 없습니다."));
		}
	}

	// 이동한 노드의 모든 하위 depth에 delta를 더한다
	private void updateDescendantDepths(Long rootId, int depthDelta) {
		List<Category> all = categoryLoadPort.findAll(true);
		Map<Long, List<Category>> childrenByParentId = new HashMap<>();
		for (Category category : all) {
			if (category.getParentId() == null) {
				continue;
			}
			childrenByParentId
					.computeIfAbsent(category.getParentId(), ignored -> new ArrayList<>())
					.add(category);
		}

		ArrayDeque<Long> queue = new ArrayDeque<>();
		queue.add(rootId);
		Set<Long> visited = new HashSet<>();
		visited.add(rootId);

		while (!queue.isEmpty()) {
			Long parentId = queue.poll();
			List<Category> children = childrenByParentId.getOrDefault(parentId, List.of());
			for (Category child : children) {
				if (!visited.add(child.getCategoryId())) {
					continue;
				}
				child.changeHierarchy(child.getParentId(), child.getDepth() + depthDelta);
				categorySavePort.update(child);
				queue.add(child.getCategoryId());
			}
		}
	}

	// 부모 ID로 부모 UUID를 조회한다
	private String resolveParentUuid(Long parentId) {
		if (parentId == null) {
			return null;
		}
		return categoryLoadPort.findById(parentId)
				.map(Category::getCategoryUuid)
				.orElse(null);
	}

	// Domain을 요약 DTO로 변환한다
	private CategorySummaryDto toSummary(Category category, String parentUuid) {
		return CategorySummaryDto.builder()
				.categoryUuid(category.getCategoryUuid())
				.categoryName(category.getCategoryName())
				.parentUuid(parentUuid)
				.sortOrder(category.getSortOrder())
				.depth(category.getDepth())
				.active(category.isActive())
				.build();
	}

	// 카테고리명 필수·trim
	private String requireName(String categoryName) {
		if (categoryName == null || categoryName.isBlank()) {
			throw new IllegalArgumentException("카테고리명은 필수입니다.");
		}
		return categoryName.trim();
	}

	// 활성 자리 번호 (1 ~ 활성수+1)로 보정
	private int resolveActivePosition(Integer requestedSortOrder, int activeCount) {
		if (requestedSortOrder == null) {
			return activeCount + 1;
		}
		if (requestedSortOrder < 1) {
			throw new IllegalArgumentException("노출 순서는 1 이상이어야 합니다.");
		}
		return Math.min(requestedSortOrder, activeCount + 1);
	}

	// 현재 형제 상태로 활성→비활성 순 1..N 재번호
	private void compactSiblings(Long parentId) {
		persistSiblingOrders(
				loadActiveSiblingsSorted(parentId, null),
				loadInactiveSiblingsSorted(parentId)
		);
	}

	// 활성·비활성 목록에 1부터 연속 번호 부여 후 저장
	private void persistSiblingOrders(List<Category> orderedActives, List<Category> orderedInactives) {
		int order = 1;
		for (Category category : orderedActives) {
			if (category.getSortOrder() != order) {
				category.changeSortOrder(order);
				categorySavePort.update(category);
			}
			order++;
		}
		for (Category category : orderedInactives) {
			if (category.getSortOrder() != order) {
				category.changeSortOrder(order);
				categorySavePort.update(category);
			}
			order++;
		}
	}

	// 활성 형제 (sortOrder 오름차순)
	private List<Category> loadActiveSiblingsSorted(Long parentId, Long excludeCategoryId) {
		return loadSiblings(parentId).stream()
				.filter(Category::isActive)
				.filter(sibling -> excludeCategoryId == null
						|| !sibling.getCategoryId().equals(excludeCategoryId))
				.sorted(Comparator.comparingInt(Category::getSortOrder)
						.thenComparing(Category::getCategoryId))
				.toList();
	}

	// 비활성 형제 (먼저 비활성화한 순 → 앞 번호)
	private List<Category> loadInactiveSiblingsSorted(Long parentId) {
		return loadSiblings(parentId).stream()
				.filter(sibling -> !sibling.isActive())
				.sorted(Comparator.comparing(Category::getDeletedAt, Comparator.nullsLast(Comparator.naturalOrder()))
						.thenComparing(Category::getCategoryId))
				.toList();
	}

	// 같은 부모 형제 목록 (비활성 포함)
	private List<Category> loadSiblings(Long parentId) {
		return parentId == null
				? categoryLoadPort.findRoots(true)
				: categoryLoadPort.findChildren(parentId, true);
	}

	// 부모 해석 결과
	private record ParentResolution(Long parentId, int depth) {
	}
}
