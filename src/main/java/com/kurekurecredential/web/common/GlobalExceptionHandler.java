package com.kurekurecredential.web.common;

import io.jsonwebtoken.JwtException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
		List<FieldErrorResponse> details = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> new FieldErrorResponse(error.getField(), error.getDefaultMessage()))
				.toList();
		return ResponseEntity.badRequest()
				.body(new ErrorResponse("VALIDATION_ERROR", "入力内容に誤りがあります。", details));
	}

	@ExceptionHandler(ConflictException.class)
	ResponseEntity<ErrorResponse> handleConflict(ConflictException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ErrorResponse.of("CONFLICT", exception.getMessage()));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ErrorResponse.of("NOT_FOUND", exception.getMessage()));
	}

	@ExceptionHandler(BadRequestException.class)
	ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException exception) {
		return ResponseEntity.badRequest()
				.body(ErrorResponse.of("BAD_REQUEST", exception.getMessage()));
	}

	@ExceptionHandler({
			HttpMessageNotReadableException.class,
			MethodArgumentTypeMismatchException.class
	})
	ResponseEntity<ErrorResponse> handleInvalidRequest() {
		return ResponseEntity.badRequest()
				.body(ErrorResponse.of("BAD_REQUEST", "リクエストの形式または値が不正です。"));
	}

	@ExceptionHandler(ForbiddenException.class)
	ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException exception) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(ErrorResponse.of("FORBIDDEN", exception.getMessage()));
	}

	@ExceptionHandler(BadCredentialsException.class)
	ResponseEntity<ErrorResponse> handleBadCredentials() {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ErrorResponse.of("BAD_CREDENTIALS", "メールアドレスまたはパスワードが正しくありません。"));
	}

	@ExceptionHandler(JwtException.class)
	ResponseEntity<ErrorResponse> handleJwtException() {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ErrorResponse.of("INVALID_TOKEN", "認証トークンが不正です。"));
	}
}
