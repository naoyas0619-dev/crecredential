package com.kurekurecredential.web.studyplan;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record CreateStudyPlanRequest(
		@NotBlank(message = "学習計画タイトルは必須です。")
		@Size(max = 255, message = "学習計画タイトルは255文字以内にしてください。")
		String title,

		@NotNull(message = "開始日は必須です。")
		LocalDate startDate,

		@NotNull(message = "終了日は必須です。")
		LocalDate endDate,

		@Min(value = 0, message = "総予定学習時間は0時間以上にしてください。")
		Integer totalPlannedHours,

		String memo,

		@NotEmpty(message = "学習計画項目を1件以上登録してください。")
		List<@Valid CreateStudyPlanItemRequest> items) {
}
