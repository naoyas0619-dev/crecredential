package com.kurekurecredential.web.studyplan;

import com.kurekurecredential.domain.study.StudyPhase;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateStudyPlanItemRequest(
		@NotNull(message = "週番号は必須です。")
		@Min(value = 1, message = "週番号は1以上にしてください。")
		Integer weekNumber,

		@NotNull(message = "学習フェーズは必須です。")
		StudyPhase phase,

		@NotBlank(message = "学習テーマは必須です。")
		@Size(max = 255, message = "学習テーマは255文字以内にしてください。")
		String title,

		String description,

		@NotNull(message = "予定学習時間は必須です。")
		@Min(value = 0, message = "予定学習時間は0時間以上にしてください。")
		Integer plannedHours,

		@NotNull(message = "模擬試験推奨フラグは必須です。")
		Boolean mockExamRecommended,

		LocalDate recommendedStartDate,

		LocalDate recommendedEndDate) {
}
