package com.kurekurecredential.service.progress;

import com.kurekurecredential.domain.certification.UserCertificationGoal;
import com.kurekurecredential.domain.study.TaskStatus;
import com.kurekurecredential.repository.MockExamResultRepository;
import com.kurekurecredential.repository.StudyLogRepository;
import com.kurekurecredential.repository.StudyPlanItemRepository;
import com.kurekurecredential.repository.StudyTaskRepository;
import com.kurekurecredential.repository.UserCertificationGoalRepository;
import com.kurekurecredential.web.common.ForbiddenException;
import com.kurekurecredential.web.common.ResourceNotFoundException;
import com.kurekurecredential.web.progress.LatestMockExamResultResponse;
import com.kurekurecredential.web.progress.ProgressSummaryResponse;
import com.kurekurecredential.web.progress.TaskProgressSummaryResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProgressSummaryService {

	private static final int MINUTES_PER_HOUR = 60;

	private final UserCertificationGoalRepository goalRepository;
	private final StudyPlanItemRepository studyPlanItemRepository;
	private final StudyLogRepository studyLogRepository;
	private final StudyTaskRepository studyTaskRepository;
	private final MockExamResultRepository mockExamResultRepository;

	@Transactional(readOnly = true)
	public ProgressSummaryResponse getSummary(Long userId, Long goalId) {
		UserCertificationGoal goal = findOwnedGoal(userId, goalId);

		long plannedStudyMinutes =
				studyPlanItemRepository.sumPlannedHoursByGoalId(goalId)
						* MINUTES_PER_HOUR;
		long actualStudyMinutes =
				studyLogRepository.sumStudyMinutesByGoalId(goalId);
		long totalTasks = studyTaskRepository.countByGoalId(goalId);
		long doneTasks = studyTaskRepository.countByGoalIdAndStatus(
				goalId,
				TaskStatus.DONE);

		TaskProgressSummaryResponse taskSummary =
				new TaskProgressSummaryResponse(
						totalTasks,
						doneTasks,
						totalTasks - doneTasks,
						calculateRate(doneTasks, totalTasks));
		LatestMockExamResultResponse latestMockExamResult =
				mockExamResultRepository
						.findFirstByGoalIdOrderByExamDateDescIdDesc(goalId)
						.map(LatestMockExamResultResponse::from)
						.orElse(null);

		return new ProgressSummaryResponse(
				goal.getId(),
				goal.getCertification().getName(),
				goal.getTargetExamDate(),
				ChronoUnit.DAYS.between(
						LocalDate.now(),
						goal.getTargetExamDate()),
				plannedStudyMinutes,
				actualStudyMinutes,
				calculateRate(actualStudyMinutes, plannedStudyMinutes),
				taskSummary,
				latestMockExamResult);
	}

	private UserCertificationGoal findOwnedGoal(Long userId, Long goalId) {
		UserCertificationGoal goal = goalRepository.findById(goalId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"資格目標が見つかりません。id=" + goalId));
		if (!goal.getUser().getId().equals(userId)) {
			throw new ForbiddenException(
					"この資格目標の進捗を参照する権限がありません。");
		}
		return goal;
	}

	private BigDecimal calculateRate(long numerator, long denominator) {
		if (denominator == 0) {
			return BigDecimal.ZERO.setScale(2);
		}
		return BigDecimal.valueOf(numerator)
				.multiply(BigDecimal.valueOf(100))
				.divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
	}
}
