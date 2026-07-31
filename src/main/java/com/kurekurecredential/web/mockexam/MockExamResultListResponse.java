package com.kurekurecredential.web.mockexam;

import java.util.List;

public record MockExamResultListResponse(
		List<MockExamResultSummaryResponse> items,
		long total) {

	public static MockExamResultListResponse of(
			List<MockExamResultSummaryResponse> items) {
		return new MockExamResultListResponse(items, items.size());
	}
}
