package com.kurekurecredential.web.goal;

import com.kurekurecredential.domain.certification.CurrentLevel;
import com.kurekurecredential.domain.certification.GoalStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UpdateCertificationGoalRequest(
		@NotNull(message = "目標試験日は必須です。")
		LocalDate targetExamDate,

		@NotNull(message = "週の学習時間は必須です。")
		@Min(value = 1, message = "週の学習時間は1時間以上にしてください。")
		Integer weeklyStudyHours,

		@NotNull(message = "現在レベルは必須です。")
		CurrentLevel currentLevel,

		@NotNull(message = "学習開始日は必須です。")
		LocalDate studyStartDate,

		@NotNull(message = "ステータスは必須です。")
		GoalStatus status) {
}
