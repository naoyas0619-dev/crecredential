package com.kurekurecredential.web.studylog;

import java.util.List;

public record StudyLogListResponse(
		List<StudyLogSummaryResponse> items,
		long total) {

	public static StudyLogListResponse of(List<StudyLogSummaryResponse> items) {
		return new StudyLogListResponse(items, items.size());
	}
}
