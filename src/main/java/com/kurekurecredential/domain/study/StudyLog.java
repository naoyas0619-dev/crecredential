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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "study_logs")
public class StudyLog extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "goal_id", nullable = false)
	private UserCertificationGoal goal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id")
	private StudyTask task;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "resource_id")
	private LearningResource resource;

	@Column(name = "studied_date", nullable = false)
	private LocalDate studiedDate;

	@Column(name = "study_minutes", nullable = false)
	private Integer studyMinutes;

	@Column(nullable = false, length = 255)
	private String title;

	@Column(columnDefinition = "text")
	private String content;

	@Column(columnDefinition = "text")
	private String reflection;

	@Enumerated(EnumType.STRING)
	@Column(name = "understanding_level", length = 50)
	private UnderstandingLevel understandingLevel;
}
