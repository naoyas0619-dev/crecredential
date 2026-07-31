package com.kurekurecredential.web.task;

import java.util.List;

public record StudyTaskListResponse(
		List<StudyTaskSummaryResponse> items,
		long total) {

	public static StudyTaskListResponse of(List<StudyTaskSummaryResponse> items) {
		return new StudyTaskListResponse(items, items.size());
	}
}
