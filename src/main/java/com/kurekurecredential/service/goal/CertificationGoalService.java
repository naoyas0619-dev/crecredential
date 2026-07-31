package com.kurekurecredential.service.goal;

import com.kurekurecredential.domain.certification.Certification;
import com.kurekurecredential.domain.certification.GoalStatus;
import com.kurekurecredential.domain.certification.UserCertificationGoal;
import com.kurekurecredential.domain.user.UserAccount;
import com.kurekurecredential.repository.CertificationRepository;
import com.kurekurecredential.repository.UserAccountRepository;
import com.kurekurecredential.repository.UserCertificationGoalRepository;
import com.kurekurecredential.web.common.BadRequestException;
import com.kurekurecredential.web.common.ForbiddenException;
import com.kurekurecredential.web.common.ResourceNotFoundException;
import com.kurekurecredential.web.goal.CertificationGoalListResponse;
import com.kurekurecredential.web.goal.CertificationGoalResponse;
import com.kurekurecredential.web.goal.CertificationGoalSummaryResponse;
import com.kurekurecredential.web.goal.CreateCertificationGoalRequest;
import com.kurekurecredential.web.goal.UpdateCertificationGoalRequest;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CertificationGoalService {

	private final UserCertificationGoalRepository goalRepository;
	private final CertificationRepository certificationRepository;
	private final UserAccountRepository userAccountRepository;

	@Transactional
	public CertificationGoalResponse create(
			Long userId,
			CreateCertificationGoalRequest request) {
		validateDateRange(request.studyStartDate(), request.targetExamDate());

		Certification certification = certificationRepository.findById(request.certificationId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"資格が見つかりません。id=" + request.certificationId()));
		UserAccount user = userAccountRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"ユーザーが見つかりません。id=" + userId));

		UserCertificationGoal goal = new UserCertificationGoal();
		goal.setUser(user);
		goal.setCertification(certification);
		goal.setTargetExamDate(request.targetExamDate());
		goal.setWeeklyStudyHours(request.weeklyStudyHours());
		goal.setCurrentLevel(request.currentLevel());
		goal.setStudyStartDate(request.studyStartDate());
		goal.setStatus(request.status());

		return CertificationGoalResponse.from(goalRepository.save(goal));
	}

	@Transactional(readOnly = true)
	public CertificationGoalListResponse findAll(Long userId, GoalStatus status) {
		List<UserCertificationGoal> goals = status == null
				? goalRepository.findByUserIdOrderByTargetExamDateAscIdAsc(userId)
				: goalRepository.findByUserIdAndStatusOrderByTargetExamDateAscIdAsc(userId, status);

		List<CertificationGoalSummaryResponse> items = goals.stream()
				.map(CertificationGoalSummaryResponse::from)
				.toList();
		return CertificationGoalListResponse.of(items);
	}

	@Transactional(readOnly = true)
	public CertificationGoalResponse findById(Long userId, Long goalId) {
		return CertificationGoalResponse.from(findOwnedGoal(userId, goalId));
	}

	@Transactional
	public CertificationGoalResponse update(
			Long userId,
			Long goalId,
			UpdateCertificationGoalRequest request) {
		validateDateRange(request.studyStartDate(), request.targetExamDate());
		UserCertificationGoal goal = findOwnedGoal(userId, goalId);

		goal.setTargetExamDate(request.targetExamDate());
		goal.setWeeklyStudyHours(request.weeklyStudyHours());
		goal.setCurrentLevel(request.currentLevel());
		goal.setStudyStartDate(request.studyStartDate());
		goal.setStatus(request.status());

		return CertificationGoalResponse.from(goal);
	}

	private UserCertificationGoal findOwnedGoal(Long userId, Long goalId) {
		UserCertificationGoal goal = goalRepository.findById(goalId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"資格目標が見つかりません。id=" + goalId));
		if (!goal.getUser().getId().equals(userId)) {
			throw new ForbiddenException("この資格目標を操作する権限がありません。");
		}
		return goal;
	}

	private void validateDateRange(LocalDate studyStartDate, LocalDate targetExamDate) {
		if (studyStartDate != null
				&& targetExamDate != null
				&& targetExamDate.isBefore(studyStartDate)) {
			throw new BadRequestException(
					"目標試験日は学習開始日以降の日付にしてください。");
		}
	}
}
