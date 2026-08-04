package com.sparta.category_service.adaptor.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.category_service.adaptor.in.web.mapper.CategoryWebMapper;
import com.sparta.category_service.adaptor.in.web.vo.CategoryTreeNodeResponseVo;
import com.sparta.category_service.application.port.in.LoadCategoryTreeUseCase;
import com.sparta.category_service.application.port.in.dto.CategoryTreeNodeDto;

import lombok.RequiredArgsConstructor;

// 카테고리 API Controller
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

	// 카테고리 트리 조회 UseCase
	private final LoadCategoryTreeUseCase loadCategoryTreeUseCase;

	// 카테고리 트리 조회
	@GetMapping("/tree")
	public List<CategoryTreeNodeResponseVo> getTree(
			@RequestParam(defaultValue = "false") boolean includeInactive
	) {
		List<CategoryTreeNodeDto> tree = loadCategoryTreeUseCase.loadTree(includeInactive);
		return CategoryWebMapper.toTreeResponse(tree);
	}
}
