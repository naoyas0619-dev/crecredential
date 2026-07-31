package com.kurekurecredential.service.resource;

import com.kurekurecredential.domain.certification.CurrentLevel;
import com.kurekurecredential.domain.study.LearningResource;
import com.kurekurecredential.domain.study.ResourceType;
import com.kurekurecredential.repository.CertificationRepository;
import com.kurekurecredential.repository.LearningResourceRepository;
import com.kurekurecredential.web.common.ResourceNotFoundException;
import com.kurekurecredential.web.resource.LearningResourceDetailResponse;
import com.kurekurecredential.web.resource.LearningResourceListResponse;
import com.kurekurecredential.web.resource.LearningResourceSummaryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LearningResourceService {

	private final LearningResourceRepository learningResourceRepository;
	private final CertificationRepository certificationRepository;

	public LearningResourceListResponse findAll(
			Long certificationId,
			ResourceType resourceType,
			CurrentLevel targetLevel) {
		if (certificationId != null && !certificationRepository.existsById(certificationId)) {
			throw new ResourceNotFoundException(
					"資格が見つかりません。id=" + certificationId);
		}

		String targetLevelValue = targetLevel == null ? null : targetLevel.name();
		List<LearningResourceSummaryResponse> items = learningResourceRepository
				.search(certificationId, resourceType, targetLevelValue)
				.stream()
				.map(LearningResourceSummaryResponse::from)
				.toList();
		return LearningResourceListResponse.of(items);
	}

	public LearningResourceDetailResponse findById(Long resourceId) {
		LearningResource resource = learningResourceRepository.findById(resourceId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"教材が見つかりません。id=" + resourceId));
		return LearningResourceDetailResponse.from(resource);
	}
}
