package com.sparta.category_service.domain.exception;

// 카테고리를 찾을 수 없을 때
public class CategoryNotFoundException extends RuntimeException {

	// 안정적 에러 코드
	public static final String CODE = "CATEGORY_NOT_FOUND";

	public CategoryNotFoundException(String message) {
		super(message);
	}

	// 에러 코드 반환
	public String getCode() {
		return CODE;
	}
}
