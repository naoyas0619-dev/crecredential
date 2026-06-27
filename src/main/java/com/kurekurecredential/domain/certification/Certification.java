package com.kurekurecredential.domain.certification;

import com.kurekurecredential.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "certifications")
public class Certification extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(nullable = false, length = 100)
	private String provider;

	@Column(nullable = false, length = 50)
	private String difficulty;

	@Column(columnDefinition = "text")
	private String description;

	@Column(name = "recommended_study_hours")
	private Integer recommendedStudyHours;

	@Column(name = "exam_format", length = 255)
	private String examFormat;

	@Column(name = "passing_score")
	private Integer passingScore;

	@Column(name = "official_url", length = 1000)
	private String officialUrl;

	@Column(name = "validity_period", length = 100)
	private String validityPeriod;
}
