package com.sparta.category_service.adaptor.in.web;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sparta.category_service.application.exception.UnauthorizedException;
import com.sparta.category_service.domain.exception.CategoryNotFoundException;
import com.sparta.category_service.domain.exception.DuplicateCategoryNameException;
import com.sparta.category_service.domain.exception.InvalidCategoryHierarchyException;

import jakarta.servlet.http.HttpServletRequest;

// API 예외를 HTTP 응답으로 변환
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 잘못된 요청 파라미터 처리
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalArgument(
			IllegalArgumentException ex,
			HttpServletRequest request
	) {
		return buildError(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", ex.getMessage(), request.getRequestURI());
	}

	// 인증 실패 처리
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Map<String, Object>> handleUnauthorized(
			UnauthorizedException ex,
			HttpServletRequest request
	) {
		return buildError(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage(), request.getRequestURI());
	}

	// 카테고리 미존재 처리
	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleCategoryNotFound(
			CategoryNotFoundException ex,
			HttpServletRequest request
	) {
		return buildError(HttpStatus.NOT_FOUND, ex.getCode(), ex.getMessage(), request.getRequestURI());
	}

	// 동일 부모 아래 카테고리명 중복 처리
	@ExceptionHandler(DuplicateCategoryNameException.class)
	public ResponseEntity<Map<String, Object>> handleDuplicateCategoryName(
			DuplicateCategoryNameException ex,
			HttpServletRequest request
	) {
		return buildError(HttpStatus.CONFLICT, ex.getCode(), ex.getMessage(), request.getRequestURI());
	}

	// 잘못된 카테고리 계층 이동 처리
	@ExceptionHandler(InvalidCategoryHierarchyException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidCategoryHierarchy(
			InvalidCategoryHierarchyException ex,
			HttpServletRequest request
	) {
		return buildError(HttpStatus.BAD_REQUEST, ex.getCode(), ex.getMessage(), request.getRequestURI());
	}

	// 공통 Error Response 생성
	private ResponseEntity<Map<String, Object>> buildError(
			HttpStatus status,
			String code,
			String message,
			String path
	) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now().toString());
		body.put("status", status.value());
		body.put("code", code);
		body.put("message", message);
		body.put("path", path);
		return ResponseEntity.status(status).body(body);
	}
}
