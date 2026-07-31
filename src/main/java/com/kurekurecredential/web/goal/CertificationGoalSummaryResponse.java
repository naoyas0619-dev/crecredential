package com.kurekurecredential.web.goal;

import com.kurekurecredential.domain.certification.GoalStatus;
import com.kurekurecredential.domain.certification.UserCertificationGoal;
import java.time.LocalDate;

public record CertificationGoalSummaryResponse(
		Long id,
		String certificationName,
		LocalDate targetExamDate,
		Integer weeklyStudyHours,
		GoalStatus status) {

	public static CertificationGoalSummaryResponse from(UserCertificationGoal goal) {
		return new CertificationGoalSummaryResponse(
				goal.getId(),
				goal.getCertification().getName(),
				goal.getTargetExamDate(),
				goal.getWeeklyStudyHours(),
				goal.getStatus());
	}
}
