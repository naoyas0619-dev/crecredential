package com.kurekurecredential.web.resource;

import java.util.List;

public record LearningResourceListResponse(
		List<LearningResourceSummaryResponse> items,
		long total) {

	public static LearningResourceListResponse of(
			List<LearningResourceSummaryResponse> items) {
		return new LearningResourceListResponse(items, items.size());
	}
}
