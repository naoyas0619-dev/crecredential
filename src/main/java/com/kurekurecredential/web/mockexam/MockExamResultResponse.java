package com.kurekurecredential.web.mockexam;

import com.kurekurecredential.domain.exam.MockExamResult;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MockExamResultResponse(
		Long id,
		Long goalId,
		LocalDate examDate,
		String examName,
		Integer score,
		Integer maxScore,
		Integer passingScore,
		Integer scoreGap,
		BigDecimal correctAnswerRate,
		String weakAreas,
		String memo) {

	public static MockExamResultResponse from(MockExamResult result) {
		return new MockExamResultResponse(
				result.getId(),
				result.getGoal().getId(),
				result.getExamDate(),
				result.getExamName(),
				result.getScore(),
				result.getMaxScore(),
				result.getPassingScore(),
				result.getScore() - result.getPassingScore(),
				result.getCorrectAnswerRate(),
				result.getWeakAreas(),
				result.getMemo());
	}
}
