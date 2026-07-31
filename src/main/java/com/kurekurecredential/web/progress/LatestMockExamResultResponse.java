package com.kurekurecredential.web.progress;

import com.kurekurecredential.domain.exam.MockExamResult;
import java.time.LocalDate;

public record LatestMockExamResultResponse(
		Long id,
		LocalDate examDate,
		String examName,
		Integer score,
		Integer passingScore,
		Integer scoreGap) {

	public static LatestMockExamResultResponse from(MockExamResult result) {
		return new LatestMockExamResultResponse(
				result.getId(),
				result.getExamDate(),
				result.getExamName(),
				result.getScore(),
				result.getPassingScore(),
				result.getScore() - result.getPassingScore());
	}
}
