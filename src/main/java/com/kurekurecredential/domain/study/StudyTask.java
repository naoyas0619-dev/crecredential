package com.kurekurecredential.domain.study;

import com.kurekurecredential.domain.certification.UserCertificationGoal;
import com.kurekurecredential.domain.common.BaseTimeEntity;
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
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "study_tasks")
public class StudyTask extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "goal_id", nullable = false)
	private UserCertificationGoal goal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_plan_item_id")
	private StudyPlanItem studyPlanItem;

	@Column(nullable = false, length = 255)
	private String title;

	@Column(columnDefinition = "text")
	private String description;

	@Column(name = "due_date")
	private LocalDate dueDate;

	@Column(name = "estimated_minutes")
	private Integer estimatedMinutes;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private TaskPriority priority;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private TaskStatus status;

	@Column(name = "completed_at")
	private OffsetDateTime completedAt;
}
