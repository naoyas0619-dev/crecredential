package com.kurekurecredential.repository;

import com.kurekurecredential.domain.exam.MockExamResult;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MockExamResultRepository extends JpaRepository<MockExamResult, Long> {

	@Query("""
			select result
			from MockExamResult result
			join fetch result.goal goal
			join fetch goal.user user
			where user.id = :userId
			  and (:goalId is null or goal.id = :goalId)
			  and (cast(:examFrom as date) is null or result.examDate >= :examFrom)
			  and (cast(:examTo as date) is null or result.examDate <= :examTo)
			order by result.examDate desc, result.id desc
			""")
	List<MockExamResult> search(
			@Param("userId") Long userId,
			@Param("goalId") Long goalId,
			@Param("examFrom") LocalDate examFrom,
			@Param("examTo") LocalDate examTo);

	@Override
	@EntityGraph(attributePaths = {"goal", "goal.user"})
	Optional<MockExamResult> findById(Long id);

	Optional<MockExamResult> findFirstByGoalIdOrderByExamDateDescIdDesc(Long goalId);
}
