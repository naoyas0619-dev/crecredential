package com.kurekurecredential.web.certification;

import java.util.List;

public record CertificationListResponse(
		List<CertificationSummaryResponse> items,
		long total) {

	public static CertificationListResponse of(List<CertificationSummaryResponse> items) {
		return new CertificationListResponse(items, items.size());
	}
}
