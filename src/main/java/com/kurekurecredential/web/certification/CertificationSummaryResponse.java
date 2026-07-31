package com.kurekurecredential.web.certification;

import com.kurekurecredential.domain.certification.Certification;

public record CertificationSummaryResponse(
		Long id,
		String name,
		String provider,
		String difficulty,
		Integer recommendedStudyHours) {

	public static CertificationSummaryResponse from(Certification certification) {
		return new CertificationSummaryResponse(
				certification.getId(),
				certification.getName(),
				certification.getProvider(),
				certification.getDifficulty(),
				certification.getRecommendedStudyHours());
	}
}
