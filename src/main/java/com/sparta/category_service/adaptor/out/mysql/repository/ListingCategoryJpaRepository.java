package com.sparta.category_service.adaptor.out.mysql.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.category_service.adaptor.out.mysql.entity.ListingCategoryEntity;

// listing_category JPA Repository (Application에서는 Adapter를 통해서만 사용)
public interface ListingCategoryJpaRepository extends JpaRepository<ListingCategoryEntity, Long> {

	// 판매 등록글 UUID로 연결 조회
	Optional<ListingCategoryEntity> findByListingUuid(String listingUuid);

	// 카테고리에 연결된 상품 매핑 존재 여부
	boolean existsByCategoryId(Long categoryId);

	// 카테고리별 연결 목록 조회
	List<ListingCategoryEntity> findByCategoryId(Long categoryId);
}
