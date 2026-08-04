package com.sparta.category_service.adaptor.out.mysql.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// category 테이블 매핑 Entity (도메인 변환은 Adapter에서 담당)
@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryEntity {

	// 카테고리 식별자 (PK)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long categoryId;

	// 부모 카테고리 식별자 (최상위면 null)
	@Column(name = "parent_id")
	private Long parentId;

	// 카테고리 UUID
	@Column(name = "category_uuid", nullable = false, unique = true, length = 36)
	private String categoryUuid;

	// 카테고리명
	@Column(name = "category_name", nullable = false, length = 50)
	private String categoryName;

	// 노출 순서
	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	// 계층 깊이 (최상위 0)
	@Column(name = "depth", nullable = false)
	private int depth;

	// 활성화 여부
	@Column(name = "active", nullable = false)
	private boolean active;

	// 생성일시
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	// 삭제일시 (소프트 삭제, 활성면 null)
	@Column(name = "deleted_at")
	private Instant deletedAt;

	// 신규 저장용 Entity 생성
	public static CategoryEntity create(
			String categoryUuid,
			Long parentId,
			String categoryName,
			int sortOrder,
			int depth,
			boolean active,
			Instant createdAt
	) {
		CategoryEntity entity = new CategoryEntity();
		entity.categoryUuid = categoryUuid;
		entity.parentId = parentId;
		entity.categoryName = categoryName;
		entity.sortOrder = sortOrder;
		entity.depth = depth;
		entity.active = active;
		entity.createdAt = createdAt;
		entity.deletedAt = null;
		return entity;
	}

	// 도메인 변경분을 Entity에 반영
	public void update(
			Long parentId,
			String categoryName,
			int sortOrder,
			int depth,
			boolean active,
			Instant deletedAt
	) {
		this.parentId = parentId;
		this.categoryName = categoryName;
		this.sortOrder = sortOrder;
		this.depth = depth;
		this.active = active;
		this.deletedAt = deletedAt;
	}
}
