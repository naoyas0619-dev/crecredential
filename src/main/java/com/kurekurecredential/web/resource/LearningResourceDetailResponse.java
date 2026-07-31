package com.kurekurecredential.web.resource;

import com.kurekurecredential.domain.study.LearningResource;
import com.kurekurecredential.domain.study.ResourceType;

public record LearningResourceDetailResponse(
		Long id,
		Long certificationId,
		String certificationName,
		String title,
		String url,
		ResourceType resourceType,
		String author,
		Integer publishedYear,
		Integer recommendationScore,
		String targetLevel,
		Integer estimatedStudyHours,
		Boolean paid,
		Boolean official,
		String memo) {

	public static LearningResourceDetailResponse from(LearningResource resource) {
		return new LearningResourceDetailResponse(
				resource.getId(),
				resource.getCertification().getId(),
				resource.getCertification().getName(),
				resource.getTitle(),
				resource.getUrl(),
				resource.getResourceType(),
				resource.getAuthor(),
				resource.getPublishedYear(),
				resource.getRecommendationScore(),
				resource.getTargetLevel(),
				resource.getEstimatedStudyHours(),
				resource.getPaid(),
				resource.getOfficial(),
				resource.getMemo());
	}
}
