package com.sparta.category_service.domain.exception;

// 부모 이동 시 순환 참조 등 계층이 잘못될 때
public class InvalidCategoryHierarchyException extends RuntimeException {

	// 안정적 에러 코드
	public static final String CODE = "INVALID_CATEGORY_HIERARCHY";

	public InvalidCategoryHierarchyException(String message) {
		super(message);
	}

	// 에러 코드 반환
	public String getCode() {
		return CODE;
	}
}
