package com.sparta.category_service.adaptor.out.mysql.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.category_service.adaptor.out.mysql.entity.CategoryEntity;

// 카테고리 JPA Repository (Application에서는 Adapter를 통해서만 사용)
public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {

	// 카테고리 UUID로 조회
	Optional<CategoryEntity> findByCategoryUuid(String categoryUuid);

	// 같은 상위 아래 동일 카테고리명 존재 여부
	boolean existsByParentIdAndCategoryName(Long parentId, String categoryName);

	// 최상위(부모 없음)에서 동일 카테고리명 존재 여부
	boolean existsByParentIdIsNullAndCategoryName(String categoryName);

	// 같은 상위 아래 동일 카테고리명 존재 여부 (본인 제외)
	boolean existsByParentIdAndCategoryNameAndCategoryIdNot(Long parentId, String categoryName, Long categoryId);

	// 최상위(부모 없음)에서 동일 카테고리명 존재 여부 (본인 제외)
	boolean existsByParentIdIsNullAndCategoryNameAndCategoryIdNot(String categoryName, Long categoryId);

	// 특정 상위의 자식 목록 (노출 순서 오름차순)
	List<CategoryEntity> findByParentIdOrderBySortOrderAscCategoryIdAsc(Long parentId);

	// 최상위 카테고리 목록 (노출 순서 오름차순)
	List<CategoryEntity> findByParentIdIsNullOrderBySortOrderAscCategoryIdAsc();

	// 하위 카테고리 존재 여부
	boolean existsByParentId(Long parentId);

	// 전체 카테고리 목록 (노출 순서 오름차순)
	List<CategoryEntity> findAllByOrderBySortOrderAscCategoryIdAsc();

	// 활성 카테고리 목록 (노출 순서 오름차순)
	List<CategoryEntity> findByActiveTrueOrderBySortOrderAscCategoryIdAsc();

	// 활성 최상위 카테고리 목록
	List<CategoryEntity> findByParentIdIsNullAndActiveTrueOrderBySortOrderAscCategoryIdAsc();

	// 활성 자식 카테고리 목록
	List<CategoryEntity> findByParentIdAndActiveTrueOrderBySortOrderAscCategoryIdAsc(Long parentId);
}
