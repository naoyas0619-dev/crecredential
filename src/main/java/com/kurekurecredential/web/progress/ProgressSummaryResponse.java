package com.kurekurecredential.web.progress;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProgressSummaryResponse(
		Long goalId,
		String certificationName,
		LocalDate targetExamDate,
		long daysUntilExam,
		long plannedStudyMinutes,
		long actualStudyMinutes,
		BigDecimal studyProgressRate,
		TaskProgressSummaryResponse taskSummary,
		LatestMockExamResultResponse latestMockExamResult) {
}
