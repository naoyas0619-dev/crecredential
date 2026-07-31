package com.kurekurecredential.web.goal;

import java.util.List;

public record CertificationGoalListResponse(
		List<CertificationGoalSummaryResponse> items,
		long total) {

	public static CertificationGoalListResponse of(
			List<CertificationGoalSummaryResponse> items) {
		return new CertificationGoalListResponse(items, items.size());
	}
}
