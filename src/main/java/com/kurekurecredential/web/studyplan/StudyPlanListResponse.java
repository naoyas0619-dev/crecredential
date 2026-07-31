package com.kurekurecredential.web.studyplan;

import java.util.List;

public record StudyPlanListResponse(
		List<StudyPlanSummaryResponse> items,
		long total) {

	public static StudyPlanListResponse of(List<StudyPlanSummaryResponse> items) {
		return new StudyPlanListResponse(items, items.size());
	}
}
