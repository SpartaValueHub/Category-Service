package com.sparta.category_service.adaptor.in.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.category_service.adaptor.in.web.mapper.CategoryWebMapper;
import com.sparta.category_service.adaptor.in.web.vo.CategorySummaryResponseVo;
import com.sparta.category_service.adaptor.in.web.vo.CategoryTreeNodeResponseVo;
import com.sparta.category_service.adaptor.in.web.vo.CreateCategoryRequestVo;
import com.sparta.category_service.application.port.in.CreateCategoryUseCase;
import com.sparta.category_service.application.port.in.LoadCategoryChildrenUseCase;
import com.sparta.category_service.application.port.in.LoadCategoryTreeUseCase;
import com.sparta.category_service.application.port.in.dto.CategorySummaryDto;
import com.sparta.category_service.application.port.in.dto.CategoryTreeNodeDto;
import com.sparta.category_service.application.port.in.dto.CreateCategoryCommand;

import lombok.RequiredArgsConstructor;

// 카테고리 API Controller
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

	// 카테고리 트리 조회 UseCase
	private final LoadCategoryTreeUseCase loadCategoryTreeUseCase;
	// 카테고리 자식 목록 조회 UseCase
	private final LoadCategoryChildrenUseCase loadCategoryChildrenUseCase;
	// 카테고리 등록 UseCase
	private final CreateCategoryUseCase createCategoryUseCase;

	// 카테고리 트리 조회
	@GetMapping("/tree")
	public List<CategoryTreeNodeResponseVo> getTree(
			@RequestParam(defaultValue = "false") boolean includeInactive
	) {
		List<CategoryTreeNodeDto> tree = loadCategoryTreeUseCase.loadTree(includeInactive);
		return CategoryWebMapper.toTreeResponse(tree);
	}

	// 자식(또는 최상위) 카테고리 목록 조회
	@GetMapping
	public List<CategorySummaryResponseVo> getChildren(
			@RequestParam(required = false) String parentUuid,
			@RequestParam(defaultValue = "false") boolean includeInactive
	) {
		List<CategorySummaryDto> children = loadCategoryChildrenUseCase.loadChildren(parentUuid, includeInactive);
		return CategoryWebMapper.toSummaryResponse(children);
	}

	// 카테고리 등록 (BO)
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategorySummaryResponseVo create(@RequestBody CreateCategoryRequestVo request) {
		CategorySummaryDto created = createCategoryUseCase.create(
				CreateCategoryCommand.builder()
						.categoryName(request.getCategoryName())
						.parentUuid(request.getParentUuid())
						.sortOrder(request.getSortOrder())
						.build()
		);
		return CategoryWebMapper.toSummaryResponse(created);
	}
}
