package com.kurekurecredential.repository;

import com.kurekurecredential.domain.exam.MockExamResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MockExamResultRepository extends JpaRepository<MockExamResult, Long> {

	List<MockExamResult> findByGoalIdOrderByExamDateDesc(Long goalId);

	Optional<MockExamResult> findFirstByGoalIdOrderByExamDateDesc(Long goalId);
}
