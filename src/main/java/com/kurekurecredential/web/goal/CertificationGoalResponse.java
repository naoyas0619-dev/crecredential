package com.kurekurecredential.web.goal;

import com.kurekurecredential.domain.certification.CurrentLevel;
import com.kurekurecredential.domain.certification.GoalStatus;
import com.kurekurecredential.domain.certification.UserCertificationGoal;
import java.time.LocalDate;

public record CertificationGoalResponse(
		Long id,
		GoalCertificationResponse certification,
		LocalDate targetExamDate,
		Integer weeklyStudyHours,
		CurrentLevel currentLevel,
		LocalDate studyStartDate,
		GoalStatus status) {

	public static CertificationGoalResponse from(UserCertificationGoal goal) {
		return new CertificationGoalResponse(
				goal.getId(),
				GoalCertificationResponse.from(goal.getCertification()),
				goal.getTargetExamDate(),
				goal.getWeeklyStudyHours(),
				goal.getCurrentLevel(),
				goal.getStudyStartDate(),
				goal.getStatus());
	}
}
