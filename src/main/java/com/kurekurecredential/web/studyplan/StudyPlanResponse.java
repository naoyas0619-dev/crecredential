package com.kurekurecredential.web.studyplan;

import com.kurekurecredential.domain.study.StudyPlan;
import com.kurekurecredential.domain.study.StudyPlanItem;
import java.time.LocalDate;
import java.util.List;

public record StudyPlanResponse(
		Long id,
		Long goalId,
		String title,
		LocalDate startDate,
		LocalDate endDate,
		Integer totalPlannedHours,
		String memo,
		List<StudyPlanItemResponse> items) {

	public static StudyPlanResponse from(
			StudyPlan plan,
			List<StudyPlanItem> planItems) {
		List<StudyPlanItemResponse> items = planItems.stream()
				.map(StudyPlanItemResponse::from)
				.toList();
		return new StudyPlanResponse(
				plan.getId(),
				plan.getGoal().getId(),
				plan.getTitle(),
				plan.getStartDate(),
				plan.getEndDate(),
				plan.getTotalPlannedHours(),
				plan.getMemo(),
				items);
	}
}
