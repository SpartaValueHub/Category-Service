package com.sparta.category_service.domain.exception;

// 하위에 카테고리가 있어 삭제(비활성)할 수 없을 때
public class CategoryHasChildrenException extends RuntimeException {

	// 안정적 에러 코드
	public static final String CODE = "CATEGORY_HAS_CHILDREN";

	public CategoryHasChildrenException(String message) {
		super(message);
	}

	// 에러 코드 반환
	public String getCode() {
		return CODE;
	}
}
