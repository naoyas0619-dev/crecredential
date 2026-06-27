package com.kurekurecredential.repository;

import com.kurekurecredential.domain.study.StudyPlanItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyPlanItemRepository extends JpaRepository<StudyPlanItem, Long> {

	List<StudyPlanItem> findByStudyPlanIdOrderByWeekNumberAsc(Long studyPlanId);
}
