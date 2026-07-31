package com.kurekurecredential.service.mockexam;

import com.kurekurecredential.domain.certification.UserCertificationGoal;
import com.kurekurecredential.domain.exam.MockExamResult;
import com.kurekurecredential.repository.MockExamResultRepository;
import com.kurekurecredential.repository.UserCertificationGoalRepository;
import com.kurekurecredential.web.common.BadRequestException;
import com.kurekurecredential.web.common.ForbiddenException;
import com.kurekurecredential.web.common.ResourceNotFoundException;
import com.kurekurecredential.web.mockexam.MockExamResultListResponse;
import com.kurekurecredential.web.mockexam.MockExamResultRequest;
import com.kurekurecredential.web.mockexam.MockExamResultResponse;
import com.kurekurecredential.web.mockexam.MockExamResultSummaryResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MockExamResultService {

	private final MockExamResultRepository mockExamResultRepository;
	private final UserCertificationGoalRepository goalRepository;

	@Transactional
	public MockExamResultResponse create(
			Long userId,
			Long goalId,
			MockExamResultRequest request) {
		UserCertificationGoal goal = findOwnedGoal(userId, goalId);
		validateRequest(goal, request);

		MockExamResult result = new MockExamResult();
		result.setGoal(goal);
		applyRequest(result, request);
		return MockExamResultResponse.from(mockExamResultRepository.save(result));
	}

	@Transactional(readOnly = true)
	public MockExamResultListResponse findAll(
			Long userId,
			Long goalId,
			LocalDate examFrom,
			LocalDate examTo) {
		validateSearchDateRange(examFrom, examTo);
		if (goalId != null) {
			findOwnedGoal(userId, goalId);
		}

		List<MockExamResultSummaryResponse> items = mockExamResultRepository
				.search(userId, goalId, examFrom, examTo)
				.stream()
				.map(MockExamResultSummaryResponse::from)
				.toList();
		return MockExamResultListResponse.of(items);
	}

	@Transactional(readOnly = true)
	public MockExamResultResponse findById(Long userId, Long resultId) {
		return MockExamResultResponse.from(findOwnedResult(userId, resultId));
	}

	@Transactional
	public MockExamResultResponse update(
			Long userId,
			Long resultId,
			MockExamResultRequest request) {
		MockExamResult result = findOwnedResult(userId, resultId);
		validateRequest(result.getGoal(), request);
		applyRequest(result, request);
		return MockExamResultResponse.from(result);
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

	private MockExamResult findOwnedResult(Long userId, Long resultId) {
		MockExamResult result = mockExamResultRepository.findById(resultId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"模擬試験結果が見つかりません。id=" + resultId));
		if (!result.getGoal().getUser().getId().equals(userId)) {
			throw new ForbiddenException("この模擬試験結果を操作する権限がありません。");
		}
		return result;
	}

	private void validateRequest(
			UserCertificationGoal goal,
			MockExamResultRequest request) {
		if (request.examDate().isBefore(goal.getStudyStartDate())
				|| request.examDate().isAfter(goal.getTargetExamDate())) {
			throw new BadRequestException(
					"受験日は資格目標の学習期間内に設定してください。");
		}
		if (request.score() > request.maxScore()) {
			throw new BadRequestException("得点は満点以下にしてください。");
		}
		if (request.passingScore() > request.maxScore()) {
			throw new BadRequestException("合格ラインは満点以下にしてください。");
		}
	}

	private void validateSearchDateRange(
			LocalDate examFrom,
			LocalDate examTo) {
		if (examFrom != null
				&& examTo != null
				&& examTo.isBefore(examFrom)) {
			throw new BadRequestException(
					"受験日終了は受験日開始以降にしてください。");
		}
	}

	private void applyRequest(
			MockExamResult result,
			MockExamResultRequest request) {
		result.setExamDate(request.examDate());
		result.setExamName(request.examName());
		result.setScore(request.score());
		result.setMaxScore(request.maxScore());
		result.setPassingScore(request.passingScore());
		result.setCorrectAnswerRate(request.correctAnswerRate());
		result.setWeakAreas(request.weakAreas());
		result.setMemo(request.memo());
	}
}
