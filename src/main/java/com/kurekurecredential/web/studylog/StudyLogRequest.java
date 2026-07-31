package com.kurekurecredential.web.studylog;

import com.kurekurecredential.domain.study.UnderstandingLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record StudyLogRequest(
		Long taskId,

		Long resourceId,

		@NotNull(message = "学習日は必須です。")
		LocalDate studiedDate,

		@NotNull(message = "学習時間は必須です。")
		@Min(value = 1, message = "学習時間は1分以上にしてください。")
		Integer studyMinutes,

		@NotBlank(message = "学習ログタイトルは必須です。")
		@Size(max = 255, message = "学習ログタイトルは255文字以内にしてください。")
		String title,

		String content,

		String reflection,

		UnderstandingLevel understandingLevel) {
}
