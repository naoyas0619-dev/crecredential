package com.kurekurecredential.service.studyplan;

import com.kurekurecredential.domain.certification.UserCertificationGoal;
import com.kurekurecredential.domain.study.StudyPlan;
import com.kurekurecredential.domain.study.StudyPlanItem;
import com.kurekurecredential.repository.StudyPlanItemRepository;
import com.kurekurecredential.repository.StudyPlanRepository;
import com.kurekurecredential.repository.UserCertificationGoalRepository;
import com.kurekurecredential.web.common.BadRequestException;
import com.kurekurecredential.web.common.ForbiddenException;
import com.kurekurecredential.web.common.ResourceNotFoundException;
import com.kurekurecredential.web.studyplan.CreateStudyPlanItemRequest;
import com.kurekurecredential.web.studyplan.CreateStudyPlanRequest;
import com.kurekurecredential.web.studyplan.StudyPlanListResponse;
import com.kurekurecredential.web.studyplan.StudyPlanResponse;
import com.kurekurecredential.web.studyplan.StudyPlanSummaryResponse;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StudyPlanService {

	private final StudyPlanRepository studyPlanRepository;
	private final StudyPlanItemRepository studyPlanItemRepository;
	private final UserCertificationGoalRepository goalRepository;

	@Transactional
	public StudyPlanResponse create(
			Long userId,
			Long goalId,
			CreateStudyPlanRequest request) {
		UserCertificationGoal goal = findOwnedGoal(userId, goalId);
		validatePlan(goal, request);

		StudyPlan plan = new StudyPlan();
		plan.setGoal(goal);
		plan.setTitle(request.title());
		plan.setStartDate(request.startDate());
		plan.setEndDate(request.endDate());
		plan.setTotalPlannedHours(request.totalPlannedHours());
		plan.setMemo(request.memo());
		StudyPlan savedPlan = studyPlanRepository.save(plan);

		List<StudyPlanItem> items = request.items().stream()
				.map(itemRequest -> toEntity(savedPlan, itemRequest))
				.toList();
		List<StudyPlanItem> savedItems = studyPlanItemRepository.saveAll(items);

		return StudyPlanResponse.from(savedPlan, sortItems(savedItems));
	}

	@Transactional(readOnly = true)
	public StudyPlanListResponse findAll(Long userId, Long goalId) {
		findOwnedGoal(userId, goalId);
		List<StudyPlanSummaryResponse> items = studyPlanRepository
				.findByGoalIdOrderByStartDateAscIdAsc(goalId)
				.stream()
				.map(StudyPlanSummaryResponse::from)
				.toList();
		return StudyPlanListResponse.of(items);
	}

	@Transactional(readOnly = true)
	public StudyPlanResponse findById(Long userId, Long studyPlanId) {
		StudyPlan plan = studyPlanRepository.findById(studyPlanId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"学習計画が見つかりません。id=" + studyPlanId));
		if (!plan.getGoal().getUser().getId().equals(userId)) {
			throw new ForbiddenException("この学習計画を参照する権限がありません。");
		}

		List<StudyPlanItem> items = studyPlanItemRepository
				.findByStudyPlanIdOrderByWeekNumberAsc(studyPlanId);
		return StudyPlanResponse.from(plan, items);
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

	private void validatePlan(
			UserCertificationGoal goal,
			CreateStudyPlanRequest request) {
		if (request.endDate().isBefore(request.startDate())) {
			throw new BadRequestException("学習計画の終了日は開始日以降にしてください。");
		}
		if (request.startDate().isBefore(goal.getStudyStartDate())
				|| request.endDate().isAfter(goal.getTargetExamDate())) {
			throw new BadRequestException("学習計画は資格目標の学習期間内に設定してください。");
		}

		Set<Integer> weekNumbers = new HashSet<>();
		for (CreateStudyPlanItemRequest item : request.items()) {
			if (!weekNumbers.add(item.weekNumber())) {
				throw new BadRequestException(
						"学習計画項目の週番号が重複しています。weekNumber=" + item.weekNumber());
			}
			validateItemDateRange(request.startDate(), request.endDate(), item);
		}
	}

	private void validateItemDateRange(
			LocalDate planStartDate,
			LocalDate planEndDate,
			CreateStudyPlanItemRequest item) {
		LocalDate itemStartDate = item.recommendedStartDate();
		LocalDate itemEndDate = item.recommendedEndDate();

		if (itemStartDate != null
				&& itemEndDate != null
				&& itemEndDate.isBefore(itemStartDate)) {
			throw new BadRequestException(
					"学習計画項目の推奨終了日は推奨開始日以降にしてください。weekNumber="
							+ item.weekNumber());
		}
		if ((itemStartDate != null
				&& (itemStartDate.isBefore(planStartDate)
						|| itemStartDate.isAfter(planEndDate)))
				|| (itemEndDate != null
				&& (itemEndDate.isBefore(planStartDate)
						|| itemEndDate.isAfter(planEndDate)))) {
			throw new BadRequestException(
					"学習計画項目の推奨期間は学習計画の期間内に設定してください。weekNumber="
							+ item.weekNumber());
		}
	}

	private StudyPlanItem toEntity(
			StudyPlan plan,
			CreateStudyPlanItemRequest request) {
		StudyPlanItem item = new StudyPlanItem();
		item.setStudyPlan(plan);
		item.setWeekNumber(request.weekNumber());
		item.setPhase(request.phase());
		item.setTitle(request.title());
		item.setDescription(request.description());
		item.setPlannedHours(request.plannedHours());
		item.setMockExamRecommended(request.mockExamRecommended());
		item.setRecommendedStartDate(request.recommendedStartDate());
		item.setRecommendedEndDate(request.recommendedEndDate());
		return item;
	}

	private List<StudyPlanItem> sortItems(List<StudyPlanItem> items) {
		return items.stream()
				.sorted((first, second) -> {
					int weekComparison = first.getWeekNumber().compareTo(second.getWeekNumber());
					if (weekComparison != 0) {
						return weekComparison;
					}
					return first.getId().compareTo(second.getId());
				})
				.toList();
	}
}
