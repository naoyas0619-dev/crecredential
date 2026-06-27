package com.kurekurecredential.repository;

import com.kurekurecredential.domain.study.StudyTask;
import com.kurekurecredential.domain.study.TaskStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {

	List<StudyTask> findByGoalId(Long goalId);

	List<StudyTask> findByGoalIdAndStatus(Long goalId, TaskStatus status);

	long countByGoalIdAndStatus(Long goalId, TaskStatus status);
}
