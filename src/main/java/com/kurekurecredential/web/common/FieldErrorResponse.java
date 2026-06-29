package com.kurekurecredential.web.common;

public record FieldErrorResponse(
		String field,
		String message) {
}
