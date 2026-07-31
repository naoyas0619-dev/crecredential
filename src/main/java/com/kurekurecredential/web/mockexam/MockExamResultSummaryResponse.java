package com.kurekurecredential.web.mockexam;

import com.kurekurecredential.domain.exam.MockExamResult;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MockExamResultSummaryResponse(
		Long id,
		Long goalId,
		LocalDate examDate,
		String examName,
		Integer score,
		Integer maxScore,
		Integer passingScore,
		Integer scoreGap,
		BigDecimal correctAnswerRate) {

	public static MockExamResultSummaryResponse from(MockExamResult result) {
		return new MockExamResultSummaryResponse(
				result.getId(),
				result.getGoal().getId(),
				result.getExamDate(),
				result.getExamName(),
				result.getScore(),
				result.getMaxScore(),
				result.getPassingScore(),
				result.getScore() - result.getPassingScore(),
				result.getCorrectAnswerRate());
	}
}
