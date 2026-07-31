package com.kurekurecredential.web.task;

import com.kurekurecredential.domain.study.StudyTask;
import com.kurekurecredential.domain.study.TaskPriority;
import com.kurekurecredential.domain.study.TaskStatus;
import java.time.LocalDate;

public record StudyTaskSummaryResponse(
		Long id,
		Long goalId,
		Long studyPlanItemId,
		String title,
		LocalDate dueDate,
		Integer estimatedMinutes,
		TaskPriority priority,
		TaskStatus status) {

	public static StudyTaskSummaryResponse from(StudyTask task) {
		Long planItemId = task.getStudyPlanItem() == null
				? null
				: task.getStudyPlanItem().getId();
		return new StudyTaskSummaryResponse(
				task.getId(),
				task.getGoal().getId(),
				planItemId,
				task.getTitle(),
				task.getDueDate(),
				task.getEstimatedMinutes(),
				task.getPriority(),
				task.getStatus());
	}
}
