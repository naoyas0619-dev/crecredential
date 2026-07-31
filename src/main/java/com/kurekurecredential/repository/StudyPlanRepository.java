package com.kurekurecredential.repository;

import com.kurekurecredential.domain.study.StudyPlan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {

	@EntityGraph(attributePaths = {"goal", "goal.user"})
	List<StudyPlan> findByGoalIdOrderByStartDateAscIdAsc(Long goalId);

	@Override
	@EntityGraph(attributePaths = {"goal", "goal.user"})
	Optional<StudyPlan> findById(Long id);
}
