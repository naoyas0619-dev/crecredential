package com.kurekurecredential.repository;

import com.kurekurecredential.domain.certification.GoalStatus;
import com.kurekurecredential.domain.certification.UserCertificationGoal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCertificationGoalRepository extends JpaRepository<UserCertificationGoal, Long> {

	@EntityGraph(attributePaths = "certification")
	List<UserCertificationGoal> findByUserIdOrderByTargetExamDateAscIdAsc(Long userId);

	@EntityGraph(attributePaths = "certification")
	List<UserCertificationGoal> findByUserIdAndStatusOrderByTargetExamDateAscIdAsc(
			Long userId,
			GoalStatus status);

	@Override
	@EntityGraph(attributePaths = {"user", "certification"})
	Optional<UserCertificationGoal> findById(Long id);
}
