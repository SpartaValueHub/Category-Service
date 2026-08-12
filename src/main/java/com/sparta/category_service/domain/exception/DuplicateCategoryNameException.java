package com.sparta.category_service.domain.exception;

// 같은 부모 아래 카테고리명이 이미 존재할 때
public class DuplicateCategoryNameException extends RuntimeException {

	// 안정적 에러 코드
	public static final String CODE = "DUPLICATE_CATEGORY_NAME";

	public DuplicateCategoryNameException(String message) {
		super(message);
	}

	// 에러 코드 반환
	public String getCode() {
		return CODE;
	}
}
