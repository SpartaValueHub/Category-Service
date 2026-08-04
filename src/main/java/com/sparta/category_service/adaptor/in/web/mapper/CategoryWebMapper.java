package com.sparta.category_service.adaptor.in.web.mapper;

import java.util.List;

import com.sparta.category_service.adaptor.in.web.vo.CategorySummaryResponseVo;
import com.sparta.category_service.adaptor.in.web.vo.CategoryTreeNodeResponseVo;
import com.sparta.category_service.application.port.in.dto.CategorySummaryDto;
import com.sparta.category_service.application.port.in.dto.CategoryTreeNodeDto;

// 카테고리 DTO -> 응답 VO 변환
public final class CategoryWebMapper {

	private CategoryWebMapper() {
	}

	// 트리 노드 목록을 응답 VO 목록으로 변환한다
	public static List<CategoryTreeNodeResponseVo> toTreeResponse(List<CategoryTreeNodeDto> nodes) {
		return nodes.stream()
				.map(CategoryWebMapper::toTreeNodeResponse)
				.toList();
	}

	// 요약 목록을 응답 VO 목록으로 변환한다
	public static List<CategorySummaryResponseVo> toSummaryResponse(List<CategorySummaryDto> items) {
		return items.stream()
				.map(CategoryWebMapper::toSummaryResponse)
				.toList();
	}

	// 트리 노드 DTO를 응답 VO로 변환한다
	private static CategoryTreeNodeResponseVo toTreeNodeResponse(CategoryTreeNodeDto node) {
		return CategoryTreeNodeResponseVo.builder()
				.categoryUuid(node.getCategoryUuid())
				.categoryName(node.getCategoryName())
				.parentUuid(node.getParentUuid())
				.sortOrder(node.getSortOrder())
				.depth(node.getDepth())
				.active(node.isActive())
				.children(toTreeResponse(node.getChildren()))
				.build();
	}

	// 요약 DTO를 응답 VO로 변환한다
	private static CategorySummaryResponseVo toSummaryResponse(CategorySummaryDto item) {
		return CategorySummaryResponseVo.builder()
				.categoryUuid(item.getCategoryUuid())
				.categoryName(item.getCategoryName())
				.parentUuid(item.getParentUuid())
				.sortOrder(item.getSortOrder())
				.depth(item.getDepth())
				.active(item.isActive())
				.build();
	}
}
