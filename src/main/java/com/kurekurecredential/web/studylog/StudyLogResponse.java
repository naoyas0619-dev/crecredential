package com.kurekurecredential.web.studylog;

import com.kurekurecredential.domain.study.StudyLog;
import com.kurekurecredential.domain.study.UnderstandingLevel;
import java.time.LocalDate;

public record StudyLogResponse(
		Long id,
		Long goalId,
		Long taskId,
		Long resourceId,
		LocalDate studiedDate,
		Integer studyMinutes,
		String title,
		String content,
		String reflection,
		UnderstandingLevel understandingLevel) {

	public static StudyLogResponse from(StudyLog log) {
		Long taskId = log.getTask() == null ? null : log.getTask().getId();
		Long resourceId = log.getResource() == null ? null : log.getResource().getId();
		return new StudyLogResponse(
				log.getId(),
				log.getGoal().getId(),
				taskId,
				resourceId,
				log.getStudiedDate(),
				log.getStudyMinutes(),
				log.getTitle(),
				log.getContent(),
				log.getReflection(),
				log.getUnderstandingLevel());
	}
}
