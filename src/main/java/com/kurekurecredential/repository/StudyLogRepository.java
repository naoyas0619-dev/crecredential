package com.kurekurecredential.repository;

import com.kurekurecredential.domain.study.StudyLog;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyLogRepository extends JpaRepository<StudyLog, Long> {

	List<StudyLog> findByGoalId(Long goalId);

	List<StudyLog> findByGoalIdAndStudiedDateBetween(Long goalId, LocalDate from, LocalDate to);

	@Query("select coalesce(sum(log.studyMinutes), 0) from StudyLog log where log.goal.id = :goalId")
	Integer sumStudyMinutesByGoalId(@Param("goalId") Long goalId);
}
