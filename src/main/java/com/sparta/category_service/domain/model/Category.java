package com.sparta.category_service.domain.model;

import java.time.Instant;
import java.util.Objects;

import lombok.Getter;

// 상품 분류용 카테고리 도메인 (대분류·중분류·브랜드를 같은 모델로 계층 관리)
@Getter
public class Category {

	// 카테고리명 최대 길이
	private static final int NAME_MAX_LENGTH = 50;

	// 카테고리 식별자 (DB PK, 신규 생성 시 null)
	private Long categoryId;
	// 카테고리 UUID
	private final String categoryUuid;
	// 부모 카테고리 식별자 (최상위면 null)
	private Long parentId;
	// 카테고리명
	private String categoryName;
	// 노출 순서 (같은 부모 아래 gap 정렬 가중치)
	private int sortOrder;
	// 계층 깊이 (최상위 0)
	private int depth;
	// 활성화 여부
	private boolean active;
	// 생성일시
	private final Instant createdAt;
	// 삭제일시 (소프트 삭제, 활성면 null)
	private Instant deletedAt;

	private Category(
			Long categoryId,
			String categoryUuid,
			Long parentId,
			String categoryName,
			int sortOrder,
			int depth,
			boolean active,
			Instant createdAt,
			Instant deletedAt
	) {
		this.categoryId = categoryId;
		this.categoryUuid = categoryUuid;
		this.parentId = parentId;
		this.categoryName = categoryName;
		this.sortOrder = sortOrder;
		this.depth = depth;
		this.active = active;
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
	}

	// 신규 카테고리 생성 (root는 parentId null, depth 0)
	public static Category create(
			String categoryUuid,
			String categoryName,
			Long parentId,
			int depth,
			int sortOrder,
			Instant createdAt
	) {
		validateUuid(categoryUuid);
		validateName(categoryName);
		validateDepth(depth, parentId);
		validateSortOrder(sortOrder);
		Objects.requireNonNull(createdAt, "생성 시각은 필수입니다.");

		return new Category(
				null,
				categoryUuid,
				parentId,
				categoryName.trim(),
				sortOrder,
				depth,
				true,
				createdAt,
				null
		);
	}

	// 저장소 조회 값으로 도메인 복원
	public static Category restore(
			Long categoryId,
			String categoryUuid,
			Long parentId,
			String categoryName,
			int sortOrder,
			int depth,
			boolean active,
			Instant createdAt,
			Instant deletedAt
	) {
		Objects.requireNonNull(categoryId, "카테고리 ID는 필수입니다.");
		validateUuid(categoryUuid);
		validateName(categoryName);
		validateDepth(depth, parentId);
		validateSortOrder(sortOrder);
		Objects.requireNonNull(createdAt, "생성 시각은 필수입니다.");

		return new Category(
				categoryId,
				categoryUuid,
				parentId,
				categoryName,
				sortOrder,
				depth,
				active,
				createdAt,
				deletedAt
		);
	}

	// 카테고리명 변경
	public void rename(String categoryName) {
		validateName(categoryName);
		this.categoryName = categoryName.trim();
	}

	// 상위 카테고리와 깊이를 함께 변경
	public void changeHierarchy(Long parentId, int depth) {
		validateDepth(depth, parentId);
		this.parentId = parentId;
		this.depth = depth;
	}

	// 노출 순서 변경
	public void changeSortOrder(int sortOrder) {
		validateSortOrder(sortOrder);
		this.sortOrder = sortOrder;
	}

	// 카테고리 활성화
	public void activate() {
		this.active = true;
		this.deletedAt = null;
	}

	// 카테고리 비활성화 (소프트 삭제)
	public void deactivate(Instant deletedAt) {
		Objects.requireNonNull(deletedAt, "삭제 시각은 필수입니다.");
		this.active = false;
		this.deletedAt = deletedAt;
	}

	// UUID 필수 검증
	private static void validateUuid(String categoryUuid) {
		if (categoryUuid == null || categoryUuid.isBlank()) {
			throw new IllegalArgumentException("카테고리 UUID는 필수입니다.");
		}
	}

	// 카테고리명 형식 검증
	private static void validateName(String categoryName) {
		if (categoryName == null || categoryName.isBlank()) {
			throw new IllegalArgumentException("카테고리명은 필수입니다.");
		}
		if (categoryName.trim().length() > NAME_MAX_LENGTH) {
			throw new IllegalArgumentException("카테고리명은 최대 50자까지 가능합니다.");
		}
	}

	// 부모-깊이 조합 검증
	private static void validateDepth(int depth, Long parentId) {
		if (depth < 0) {
			throw new IllegalArgumentException("카테고리 깊이는 0 이상이어야 합니다.");
		}
		if (parentId == null && depth != 0) {
			throw new IllegalArgumentException("최상위 카테고리의 깊이는 0이어야 합니다.");
		}
		if (parentId != null && depth < 1) {
			throw new IllegalArgumentException("하위 카테고리의 깊이는 1 이상이어야 합니다.");
		}
	}

	// 노출 순서 검증 (gap 가중치, 1 이상)
	private static void validateSortOrder(int sortOrder) {
		if (sortOrder < 1) {
			throw new IllegalArgumentException("노출 순서는 1 이상이어야 합니다.");
		}
	}
}
