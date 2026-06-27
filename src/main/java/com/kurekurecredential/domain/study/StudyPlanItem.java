package com.kurekurecredential.domain.study;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "study_plan_items")
public class StudyPlanItem extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "study_plan_id", nullable = false)
	private StudyPlan studyPlan;

	@Column(name = "week_number", nullable = false)
	private Integer weekNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private StudyPhase phase;

	@Column(nullable = false, length = 255)
	private String title;

	@Column(columnDefinition = "text")
	private String description;

	@Column(name = "planned_hours", nullable = false)
	private Integer plannedHours;

	@Column(name = "mock_exam_recommended", nullable = false)
	private Boolean mockExamRecommended = false;

	@Column(name = "recommended_start_date")
	private LocalDate recommendedStartDate;

	@Column(name = "recommended_end_date")
	private LocalDate recommendedEndDate;
}
