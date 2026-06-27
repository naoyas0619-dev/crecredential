package com.kurekurecredential.repository;

import com.kurekurecredential.domain.certification.GoalStatus;
import com.kurekurecredential.domain.certification.UserCertificationGoal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCertificationGoalRepository extends JpaRepository<UserCertificationGoal, Long> {

	List<UserCertificationGoal> findByUserId(Long userId);

	List<UserCertificationGoal> findByUserIdAndStatus(Long userId, GoalStatus status);
}
