package com.kurekurecredential.web.common;

import io.jsonwebtoken.JwtException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
