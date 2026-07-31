package com.kurekurecredential.web.studyplan;

import com.kurekurecredential.domain.study.StudyPhase;
import com.kurekurecredential.domain.study.StudyPlanItem;
import java.time.LocalDate;

public record StudyPlanItemResponse(
		Long id,
		Integer weekNumber,
		StudyPhase phase,
		String title,
		String description,
		Integer plannedHours,
		Boolean mockExamRecommended,
		LocalDate recommendedStartDate,
		LocalDate recommendedEndDate) {

	public static StudyPlanItemResponse from(StudyPlanItem item) {
		return new StudyPlanItemResponse(
				item.getId(),
				item.getWeekNumber(),
				item.getPhase(),
				item.getTitle(),
				item.getDescription(),
				item.getPlannedHours(),
				item.getMockExamRecommended(),
				item.getRecommendedStartDate(),
				item.getRecommendedEndDate());
	}
}
