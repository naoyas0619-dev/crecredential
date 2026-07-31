package com.kurekurecredential.repository;

import com.kurekurecredential.domain.study.StudyPlanItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyPlanItemRepository extends JpaRepository<StudyPlanItem, Long> {

	List<StudyPlanItem> findByStudyPlanIdOrderByWeekNumberAsc(Long studyPlanId);

	@Override
	@EntityGraph(attributePaths = {
			"studyPlan",
			"studyPlan.goal",
			"studyPlan.goal.user"
	})
	Optional<StudyPlanItem> findById(Long id);
}
