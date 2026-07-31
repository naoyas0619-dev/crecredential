package com.kurekurecredential.repository;

import com.kurekurecredential.domain.study.StudyLog;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyLogRepository extends JpaRepository<StudyLog, Long> {

	@Query("""
			select log
			from StudyLog log
			join fetch log.goal goal
			join fetch goal.user user
			left join fetch log.task task
			left join fetch log.resource resource
			where user.id = :userId
			  and (:goalId is null or goal.id = :goalId)
			  and (cast(:studiedFrom as date) is null or log.studiedDate >= :studiedFrom)
			  and (cast(:studiedTo as date) is null or log.studiedDate <= :studiedTo)
			order by log.studiedDate desc, log.id desc
			""")
	List<StudyLog> search(
			@Param("userId") Long userId,
			@Param("goalId") Long goalId,
			@Param("studiedFrom") LocalDate studiedFrom,
			@Param("studiedTo") LocalDate studiedTo);

	@Override
	@EntityGraph(attributePaths = {"goal", "goal.user", "task", "resource"})
	Optional<StudyLog> findById(Long id);

	@Query("select coalesce(sum(log.studyMinutes), 0) from StudyLog log where log.goal.id = :goalId")
	Long sumStudyMinutesByGoalId(@Param("goalId") Long goalId);
}
