package com.kurekurecredential.web.studylog;

import com.kurekurecredential.domain.study.StudyLog;
import com.kurekurecredential.domain.study.UnderstandingLevel;
import java.time.LocalDate;

public record StudyLogSummaryResponse(
		Long id,
		Long goalId,
		Long taskId,
		Long resourceId,
		LocalDate studiedDate,
		Integer studyMinutes,
		String title,
		UnderstandingLevel understandingLevel) {

	public static StudyLogSummaryResponse from(StudyLog log) {
		Long taskId = log.getTask() == null ? null : log.getTask().getId();
		Long resourceId = log.getResource() == null ? null : log.getResource().getId();
		return new StudyLogSummaryResponse(
				log.getId(),
				log.getGoal().getId(),
				taskId,
				resourceId,
				log.getStudiedDate(),
				log.getStudyMinutes(),
				log.getTitle(),
				log.getUnderstandingLevel());
	}
}
