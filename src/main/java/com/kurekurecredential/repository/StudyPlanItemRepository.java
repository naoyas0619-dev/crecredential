package com.kurekurecredential.repository;

import com.kurekurecredential.domain.study.StudyPlanItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyPlanItemRepository extends JpaRepository<StudyPlanItem, Long> {

	List<StudyPlanItem> findByStudyPlanIdOrderByWeekNumberAsc(Long studyPlanId);

	@Query("""
			select coalesce(sum(item.plannedHours), 0)
			from StudyPlanItem item
			where item.studyPlan.goal.id = :goalId
			""")
	Long sumPlannedHoursByGoalId(@Param("goalId") Long goalId);

	@Override
	@EntityGraph(attributePaths = {
			"studyPlan",
			"studyPlan.goal",
			"studyPlan.goal.user"
	})
	Optional<StudyPlanItem> findById(Long id);
}
