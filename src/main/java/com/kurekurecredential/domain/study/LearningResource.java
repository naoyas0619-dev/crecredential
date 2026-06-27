package com.kurekurecredential.domain.study;

import com.kurekurecredential.domain.certification.Certification;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "learning_resources")
public class LearningResource extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "certification_id", nullable = false)
	private Certification certification;

	@Column(nullable = false, length = 255)
	private String title;

	@Column(length = 1000)
	private String url;

	@Enumerated(EnumType.STRING)
	@Column(name = "resource_type", nullable = false, length = 50)
	private ResourceType resourceType;

	@Column(length = 255)
	private String author;

	@Column(name = "published_year")
	private Integer publishedYear;

	@Column(name = "recommendation_score")
	private Integer recommendationScore;

	@Column(name = "target_level", length = 50)
	private String targetLevel;

	@Column(name = "estimated_study_hours")
	private Integer estimatedStudyHours;

	@Column(nullable = false)
	private Boolean paid = false;

	@Column(nullable = false)
	private Boolean official = false;

	@Column(columnDefinition = "text")
	private String memo;
}
