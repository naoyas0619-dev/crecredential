package com.kurekurecredential.repository;

import com.kurekurecredential.domain.study.StudyPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {

	List<StudyPlan> findByGoalId(Long goalId);
}
