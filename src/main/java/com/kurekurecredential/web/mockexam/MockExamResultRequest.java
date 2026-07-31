package com.kurekurecredential.web.mockexam;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MockExamResultRequest(
		@NotNull(message = "受験日は必須です。")
		LocalDate examDate,

		@NotBlank(message = "試験名は必須です。")
		@Size(max = 255, message = "試験名は255文字以内にしてください。")
		String examName,

		@NotNull(message = "得点は必須です。")
		@Min(value = 0, message = "得点は0以上にしてください。")
		Integer score,

		@NotNull(message = "満点は必須です。")
		@Min(value = 1, message = "満点は1以上にしてください。")
		Integer maxScore,

		@NotNull(message = "合格ラインは必須です。")
		@Min(value = 0, message = "合格ラインは0以上にしてください。")
		Integer passingScore,

		@DecimalMin(value = "0.0", message = "正答率は0以上にしてください。")
		@DecimalMax(value = "100.0", message = "正答率は100以下にしてください。")
		BigDecimal correctAnswerRate,

		String weakAreas,

		String memo) {
}
