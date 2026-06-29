package com.kurekurecredential.web.common;

import java.util.List;

public record ErrorResponse(
		String code,
		String message,
		List<FieldErrorResponse> details) {

	public static ErrorResponse of(String code, String message) {
		return new ErrorResponse(code, message, List.of());
	}
}
