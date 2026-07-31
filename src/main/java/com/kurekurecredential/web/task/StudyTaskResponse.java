package com.kurekurecredential.web.task;

import com.kurekurecredential.domain.study.StudyTask;
import com.kurekurecredential.domain.study.TaskPriority;
import com.kurekurecredential.domain.study.TaskStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record StudyTaskResponse(
		Long id,
		Long goalId,
		Long studyPlanItemId,
		String title,
		String description,
		LocalDate dueDate,
		Integer estimatedMinutes,
		TaskPriority priority,
		TaskStatus status,
		OffsetDateTime completedAt) {

	public static StudyTaskResponse from(StudyTask task) {
		Long planItemId = task.getStudyPlanItem() == null
				? null
				: task.getStudyPlanItem().getId();
		return new StudyTaskResponse(
				task.getId(),
				task.getGoal().getId(),
				planItemId,
				task.getTitle(),
				task.getDescription(),
				task.getDueDate(),
				task.getEstimatedMinutes(),
				task.getPriority(),
				task.getStatus(),
				task.getCompletedAt());
	}
}
