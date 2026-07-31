package com.kurekurecredential.web.certification;

import com.kurekurecredential.domain.certification.Certification;

public record CertificationDetailResponse(
		Long id,
		String name,
		String provider,
		String difficulty,
		String description,
		Integer recommendedStudyHours,
		String examFormat,
		Integer passingScore,
		String officialUrl,
		String validityPeriod) {

	public static CertificationDetailResponse from(Certification certification) {
		return new CertificationDetailResponse(
				certification.getId(),
				certification.getName(),
				certification.getProvider(),
				certification.getDifficulty(),
				certification.getDescription(),
				certification.getRecommendedStudyHours(),
				certification.getExamFormat(),
				certification.getPassingScore(),
				certification.getOfficialUrl(),
				certification.getValidityPeriod());
	}
}
