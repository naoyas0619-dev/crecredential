package com.kurekurecredential.web.studyplan;

import com.kurekurecredential.domain.study.StudyPlan;
import java.time.LocalDate;

public record StudyPlanSummaryResponse(
		Long id,
		Long goalId,
		String title,
		LocalDate startDate,
		LocalDate endDate,
		Integer totalPlannedHours) {

	public static StudyPlanSummaryResponse from(StudyPlan plan) {
		return new StudyPlanSummaryResponse(
				plan.getId(),
				plan.getGoal().getId(),
				plan.getTitle(),
				plan.getStartDate(),
				plan.getEndDate(),
				plan.getTotalPlannedHours());
	}
}
