package com.kurekurecredential.web.goal;

import com.kurekurecredential.domain.certification.Certification;

public record GoalCertificationResponse(
		Long id,
		String name) {

	public static GoalCertificationResponse from(Certification certification) {
		return new GoalCertificationResponse(certification.getId(), certification.getName());
	}
}
