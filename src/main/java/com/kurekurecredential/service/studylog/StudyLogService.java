package com.kurekurecredential.service.studylog;

import com.kurekurecredential.domain.certification.UserCertificationGoal;
import com.kurekurecredential.domain.study.LearningResource;
import com.kurekurecredential.domain.study.StudyLog;
import com.kurekurecredential.domain.study.StudyTask;
import com.kurekurecredential.repository.LearningResourceRepository;
import com.kurekurecredential.repository.StudyLogRepository;
import com.kurekurecredential.repository.StudyTaskRepository;
import com.kurekurecredential.repository.UserCertificationGoalRepository;
import com.kurekurecredential.web.common.BadRequestException;
import com.kurekurecredential.web.common.ForbiddenException;
import com.kurekurecredential.web.common.ResourceNotFoundException;
import com.kurekurecredential.web.studylog.StudyLogListResponse;
import com.kurekurecredential.web.studylog.StudyLogRequest;
import com.kurekurecredential.web.studylog.StudyLogResponse;
import com.kurekurecredential.web.studylog.StudyLogSummaryResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StudyLogService {

	private final StudyLogRepository studyLogRepository;
	private final StudyTaskRepository studyTaskRepository;
	private final LearningResourceRepository learningResourceRepository;
	private final UserCertificationGoalRepository goalRepository;

	@Transactional
	public StudyLogResponse create(
			Long userId,
			Long goalId,
			StudyLogRequest request) {
		UserCertificationGoal goal = findOwnedGoal(userId, goalId);
		validateStudiedDate(goal, request.studiedDate());
		StudyTask task = resolveTask(userId, goalId, request.taskId());
		LearningResource resource = resolveResource(goal, request.resourceId());

		StudyLog log = new StudyLog();
		log.setGoal(goal);
		applyRequest(log, task, resource, request);
		return StudyLogResponse.from(studyLogRepository.save(log));
	}

	@Transactional(readOnly = true)
	public StudyLogListResponse findAll(
			Long userId,
			Long goalId,
			LocalDate studiedFrom,
			LocalDate studiedTo) {
		validateSearchDateRange(studiedFrom, studiedTo);
		if (goalId != null) {
			findOwnedGoal(userId, goalId);
		}

		List<StudyLogSummaryResponse> items = studyLogRepository
				.search(userId, goalId, studiedFrom, studiedTo)
				.stream()
				.map(StudyLogSummaryResponse::from)
				.toList();
		return StudyLogListResponse.of(items);
	}

	@Transactional(readOnly = true)
	public StudyLogResponse findById(Long userId, Long logId) {
		return StudyLogResponse.from(findOwnedLog(userId, logId));
	}

	@Transactional
	public StudyLogResponse update(
			Long userId,
			Long logId,
			StudyLogRequest request) {
		StudyLog log = findOwnedLog(userId, logId);
		UserCertificationGoal goal = log.getGoal();
		validateStudiedDate(goal, request.studiedDate());
		StudyTask task = resolveTask(userId, goal.getId(), request.taskId());
		LearningResource resource = resolveResource(goal, request.resourceId());
		applyRequest(log, task, resource, request);
		return StudyLogResponse.from(log);
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

	private StudyLog findOwnedLog(Long userId, Long logId) {
		StudyLog log = studyLogRepository.findById(logId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"学習ログが見つかりません。id=" + logId));
		if (!log.getGoal().getUser().getId().equals(userId)) {
			throw new ForbiddenException("この学習ログを操作する権限がありません。");
		}
		return log;
	}

	private StudyTask resolveTask(Long userId, Long goalId, Long taskId) {
		if (taskId == null) {
			return null;
		}

		StudyTask task = studyTaskRepository.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"学習タスクが見つかりません。id=" + taskId));
		if (!task.getGoal().getUser().getId().equals(userId)) {
			throw new ForbiddenException("この学習タスクを使用する権限がありません。");
		}
		if (!task.getGoal().getId().equals(goalId)) {
			throw new BadRequestException(
					"学習タスクは指定した資格目標に属していません。");
		}
		return task;
	}

	private LearningResource resolveResource(
			UserCertificationGoal goal,
			Long resourceId) {
		if (resourceId == null) {
			return null;
		}

		LearningResource resource = learningResourceRepository.findById(resourceId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"教材が見つかりません。id=" + resourceId));
		if (!resource.getCertification().getId()
				.equals(goal.getCertification().getId())) {
			throw new BadRequestException(
					"教材は資格目標と同じ資格に紐づくものを指定してください。");
		}
		return resource;
	}

	private void validateStudiedDate(
			UserCertificationGoal goal,
			LocalDate studiedDate) {
		if (studiedDate.isBefore(goal.getStudyStartDate())
				|| studiedDate.isAfter(goal.getTargetExamDate())) {
			throw new BadRequestException(
					"学習日は資格目標の学習期間内に設定してください。");
		}
	}

	private void validateSearchDateRange(
			LocalDate studiedFrom,
			LocalDate studiedTo) {
		if (studiedFrom != null
				&& studiedTo != null
				&& studiedTo.isBefore(studiedFrom)) {
			throw new BadRequestException(
					"学習日終了日は学習日開始日以降にしてください。");
		}
	}

	private void applyRequest(
			StudyLog log,
			StudyTask task,
			LearningResource resource,
			StudyLogRequest request) {
		log.setTask(task);
		log.setResource(resource);
		log.setStudiedDate(request.studiedDate());
		log.setStudyMinutes(request.studyMinutes());
		log.setTitle(request.title());
		log.setContent(request.content());
		log.setReflection(request.reflection());
		log.setUnderstandingLevel(request.understandingLevel());
	}
}
