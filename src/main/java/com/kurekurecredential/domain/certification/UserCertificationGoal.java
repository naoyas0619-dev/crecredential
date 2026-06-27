package com.kurekurecredential.domain.certification;

import com.kurekurecredential.domain.common.BaseTimeEntity;
import com.kurekurecredential.domain.user.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_certification_goals")
public class UserCertificationGoal extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserAccount user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "certification_id", nullable = false)
	private Certification certification;

	@Column(name = "target_exam_date", nullable = false)
	private LocalDate targetExamDate;

	@Column(name = "weekly_study_hours", nullable = false)
	private Integer weeklyStudyHours;

	@Enumerated(EnumType.STRING)
	@Column(name = "current_level", nullable = false, length = 50)
	private CurrentLevel currentLevel;

	@Column(name = "study_start_date", nullable = false)
	private LocalDate studyStartDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private GoalStatus status;
}
