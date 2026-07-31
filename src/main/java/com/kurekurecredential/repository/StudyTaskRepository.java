package com.kurekurecredential.repository;

import com.kurekurecredential.domain.study.StudyTask;
import com.kurekurecredential.domain.study.TaskStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {

	@Query("""
			select task
			from StudyTask task
			join fetch task.goal goal
			join fetch goal.user user
			left join fetch task.studyPlanItem planItem
			where user.id = :userId
			  and (:goalId is null or goal.id = :goalId)
			  and (:status is null or task.status = :status)
			  and (cast(:dueFrom as date) is null or task.dueDate >= :dueFrom)
			  and (cast(:dueTo as date) is null or task.dueDate <= :dueTo)
			order by task.dueDate asc, task.id asc
			""")
	List<StudyTask> search(
			@Param("userId") Long userId,
			@Param("goalId") Long goalId,
			@Param("status") TaskStatus status,
			@Param("dueFrom") LocalDate dueFrom,
			@Param("dueTo") LocalDate dueTo);

	@Override
	@EntityGraph(attributePaths = {"goal", "goal.user", "studyPlanItem"})
	Optional<StudyTask> findById(Long id);

	long countByGoalIdAndStatus(Long goalId, TaskStatus status);
}
