package com.kurekurecredential.web.task;

import com.kurekurecredential.domain.study.TaskPriority;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateStudyTaskRequest(
		Long studyPlanItemId,

		@NotBlank(message = "タスク名は必須です。")
		@Size(max = 255, message = "タスク名は255文字以内にしてください。")
		String title,

		String description,

		LocalDate dueDate,

		@Min(value = 0, message = "見積もり時間は0分以上にしてください。")
		Integer estimatedMinutes,

		@NotNull(message = "優先度は必須です。")
		TaskPriority priority) {
}
