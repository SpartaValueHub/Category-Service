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

// listing_category 테이블 매핑 Entity (상품-카테고리 연결)
@Entity
@Table(name = "listing_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ListingCategoryEntity {

	// 상품 카테고리 식별자 (PK)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_category_id")
	private Long productCategoryId;

	// 카테고리 식별자 (category.category_id 참조)
	@Column(name = "category_id", nullable = false)
	private Long categoryId;

	// 판매 등록글 UUID
	@Column(name = "listing_uuid", nullable = false, unique = true, length = 36)
	private String listingUuid;

	// 생성일시
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	// 수정일시
	@Column(name = "updated_at")
	private Instant updatedAt;

	// 신규 저장용 Entity 생성
	public static ListingCategoryEntity create(
			Long categoryId,
			String listingUuid,
			Instant createdAt
	) {
		ListingCategoryEntity entity = new ListingCategoryEntity();
		entity.categoryId = categoryId;
		entity.listingUuid = listingUuid;
		entity.createdAt = createdAt;
		entity.updatedAt = null;
		return entity;
	}

	// 연결 카테고리 변경 시 반영
	public void updateCategory(Long categoryId, Instant updatedAt) {
		this.categoryId = categoryId;
		this.updatedAt = updatedAt;
	}
}
