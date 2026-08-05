package com.sparta.category_service.adaptor.in.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.sparta.category_service.adaptor.in.web.vo.UpdateCategoryRequestVo;
import com.sparta.category_service.application.port.in.CreateCategoryUseCase;
import com.sparta.category_service.application.port.in.DeactivateCategoryUseCase;
import com.sparta.category_service.application.port.in.LoadCategoryChildrenUseCase;
import com.sparta.category_service.application.port.in.LoadCategoryTreeUseCase;
import com.sparta.category_service.application.port.in.UpdateCategoryUseCase;
import com.sparta.category_service.application.port.in.dto.CategorySummaryDto;
import com.sparta.category_service.application.port.in.dto.CategoryTreeNodeDto;
import com.sparta.category_service.application.port.in.dto.CreateCategoryCommand;
import com.sparta.category_service.application.port.in.dto.UpdateCategoryCommand;

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
	// 카테고리 수정 UseCase
	private final UpdateCategoryUseCase updateCategoryUseCase;
	// 카테고리 삭제(비활성) UseCase
	private final DeactivateCategoryUseCase deactivateCategoryUseCase;

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

	// 카테고리 수정 (BO) - path의 UUID 대상만 부분 수정
	@PatchMapping("/{categoryUuid}")
	public CategorySummaryResponseVo update(
			@PathVariable String categoryUuid,
			@RequestBody UpdateCategoryRequestVo request
	) {
		CategorySummaryDto updated = updateCategoryUseCase.update(
				UpdateCategoryCommand.builder()
						.categoryUuid(categoryUuid)
						.categoryName(request.getCategoryName())
						.parentUuidSpecified(request.isParentUuidSpecified())
						.parentUuid(request.getParentUuid())
						.sortOrder(request.getSortOrder())
						.build()
		);
		return CategoryWebMapper.toSummaryResponse(updated);
	}

	// 카테고리 삭제(비활성) (BO) - soft delete
	@DeleteMapping("/{categoryUuid}")
	public CategorySummaryResponseVo deactivate(@PathVariable String categoryUuid) {
		CategorySummaryDto deactivated = deactivateCategoryUseCase.deactivate(categoryUuid);
		return CategoryWebMapper.toSummaryResponse(deactivated);
	}
}
