package com.kurekurecredential.web.resource;

import com.kurekurecredential.domain.study.LearningResource;
import com.kurekurecredential.domain.study.ResourceType;

public record LearningResourceSummaryResponse(
		Long id,
		Long certificationId,
		String title,
		String url,
		ResourceType resourceType,
		Integer recommendationScore,
		String targetLevel,
		Integer estimatedStudyHours,
		Boolean paid,
		Boolean official) {

	public static LearningResourceSummaryResponse from(LearningResource resource) {
		return new LearningResourceSummaryResponse(
				resource.getId(),
				resource.getCertification().getId(),
				resource.getTitle(),
				resource.getUrl(),
				resource.getResourceType(),
				resource.getRecommendationScore(),
				resource.getTargetLevel(),
				resource.getEstimatedStudyHours(),
				resource.getPaid(),
				resource.getOfficial());
	}
}
