package com.kurekurecredential.domain.exam;

import com.kurekurecredential.domain.certification.UserCertificationGoal;
import com.kurekurecredential.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mock_exam_results")
public class MockExamResult extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "goal_id", nullable = false)
	private UserCertificationGoal goal;

	@Column(name = "exam_date", nullable = false)
	private LocalDate examDate;

	@Column(name = "exam_name", nullable = false, length = 255)
	private String examName;

	@Column(nullable = false)
	private Integer score;

	@Column(name = "max_score", nullable = false)
	private Integer maxScore;

	@Column(name = "passing_score", nullable = false)
	private Integer passingScore;

	@Column(name = "correct_answer_rate", precision = 5, scale = 2)
	private BigDecimal correctAnswerRate;

	@Column(name = "weak_areas", columnDefinition = "text")
	private String weakAreas;

	@Column(columnDefinition = "text")
	private String memo;
}
