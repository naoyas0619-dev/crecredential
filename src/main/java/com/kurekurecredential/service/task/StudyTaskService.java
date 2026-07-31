package com.kurekurecredential.service.task;

import com.kurekurecredential.domain.certification.UserCertificationGoal;
import com.kurekurecredential.domain.study.StudyPlanItem;
import com.kurekurecredential.domain.study.StudyTask;
import com.kurekurecredential.domain.study.TaskStatus;
import com.kurekurecredential.repository.StudyPlanItemRepository;
import com.kurekurecredential.repository.StudyTaskRepository;
import com.kurekurecredential.repository.UserCertificationGoalRepository;
import com.kurekurecredential.web.common.BadRequestException;
import com.kurekurecredential.web.common.ForbiddenException;
import com.kurekurecredential.web.common.ResourceNotFoundException;
import com.kurekurecredential.web.task.CreateStudyTaskRequest;
import com.kurekurecredential.web.task.StudyTaskListResponse;
import com.kurekurecredential.web.task.StudyTaskResponse;
import com.kurekurecredential.web.task.StudyTaskSummaryResponse;
import com.kurekurecredential.web.task.UpdateStudyTaskRequest;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StudyTaskService {

	private final StudyTaskRepository studyTaskRepository;
	private final StudyPlanItemRepository studyPlanItemRepository;
	private final UserCertificationGoalRepository goalRepository;

	@Transactional
	public StudyTaskResponse create(
			Long userId,
			Long goalId,
			CreateStudyTaskRequest request) {
		UserCertificationGoal goal = findOwnedGoal(userId, goalId);
		validateDueDate(goal, request.dueDate());
		StudyPlanItem planItem = resolvePlanItem(
				userId,
				goalId,
				request.studyPlanItemId());

		StudyTask task = new StudyTask();
		task.setGoal(goal);
		task.setStudyPlanItem(planItem);
		task.setTitle(request.title());
		task.setDescription(request.description());
		task.setDueDate(request.dueDate());
		task.setEstimatedMinutes(request.estimatedMinutes());
		task.setPriority(request.priority());
		task.setStatus(TaskStatus.TODO);

		return StudyTaskResponse.from(studyTaskRepository.save(task));
	}

	@Transactional(readOnly = true)
	public StudyTaskListResponse findAll(
			Long userId,
			Long goalId,
			TaskStatus status,
			LocalDate dueFrom,
			LocalDate dueTo) {
		validateSearchDateRange(dueFrom, dueTo);
		if (goalId != null) {
			findOwnedGoal(userId, goalId);
		}

		List<StudyTaskSummaryResponse> items = studyTaskRepository
				.search(userId, goalId, status, dueFrom, dueTo)
				.stream()
				.map(StudyTaskSummaryResponse::from)
				.toList();
		return StudyTaskListResponse.of(items);
	}

	@Transactional(readOnly = true)
	public StudyTaskResponse findById(Long userId, Long taskId) {
		return StudyTaskResponse.from(findOwnedTask(userId, taskId));
	}

	@Transactional
	public StudyTaskResponse update(
			Long userId,
			Long taskId,
			UpdateStudyTaskRequest request) {
		StudyTask task = findOwnedTask(userId, taskId);
		validateDueDate(task.getGoal(), request.dueDate());
		StudyPlanItem planItem = resolvePlanItem(
				userId,
				task.getGoal().getId(),
				request.studyPlanItemId());

		task.setStudyPlanItem(planItem);
		task.setTitle(request.title());
		task.setDescription(request.description());
		task.setDueDate(request.dueDate());
		task.setEstimatedMinutes(request.estimatedMinutes());
		task.setPriority(request.priority());
		applyStatus(task, request.status());

		return StudyTaskResponse.from(task);
	}

	@Transactional
	public StudyTaskResponse complete(Long userId, Long taskId) {
		StudyTask task = findOwnedTask(userId, taskId);
		applyStatus(task, TaskStatus.DONE);
		return StudyTaskResponse.from(task);
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

	private StudyTask findOwnedTask(Long userId, Long taskId) {
		StudyTask task = studyTaskRepository.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"学習タスクが見つかりません。id=" + taskId));
		if (!task.getGoal().getUser().getId().equals(userId)) {
			throw new ForbiddenException("この学習タスクを操作する権限がありません。");
		}
		return task;
	}

	private StudyPlanItem resolvePlanItem(
			Long userId,
			Long goalId,
			Long studyPlanItemId) {
		if (studyPlanItemId == null) {
			return null;
		}

		StudyPlanItem planItem = studyPlanItemRepository.findById(studyPlanItemId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"学習計画項目が見つかりません。id=" + studyPlanItemId));
		UserCertificationGoal itemGoal = planItem.getStudyPlan().getGoal();
		if (!itemGoal.getUser().getId().equals(userId)) {
			throw new ForbiddenException("この学習計画項目を使用する権限がありません。");
		}
		if (!itemGoal.getId().equals(goalId)) {
			throw new BadRequestException(
					"学習計画項目は指定した資格目標に属していません。");
		}
		return planItem;
	}

	private void validateDueDate(
			UserCertificationGoal goal,
			LocalDate dueDate) {
		if (dueDate != null
				&& (dueDate.isBefore(goal.getStudyStartDate())
						|| dueDate.isAfter(goal.getTargetExamDate()))) {
			throw new BadRequestException(
					"タスク期限は資格目標の学習期間内に設定してください。");
		}
	}

	private void validateSearchDateRange(LocalDate dueFrom, LocalDate dueTo) {
		if (dueFrom != null && dueTo != null && dueTo.isBefore(dueFrom)) {
			throw new BadRequestException("期限終了日は期限開始日以降にしてください。");
		}
	}

	private void applyStatus(StudyTask task, TaskStatus status) {
		if (status == TaskStatus.DONE) {
			if (task.getCompletedAt() == null) {
				task.setCompletedAt(OffsetDateTime.now());
			}
		} else {
			task.setCompletedAt(null);
		}
		task.setStatus(status);
	}
}
