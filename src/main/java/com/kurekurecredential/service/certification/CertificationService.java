package com.kurekurecredential.service.certification;

import com.kurekurecredential.domain.certification.Certification;
import com.kurekurecredential.repository.CertificationRepository;
import com.kurekurecredential.web.certification.CertificationDetailResponse;
import com.kurekurecredential.web.certification.CertificationListResponse;
import com.kurekurecredential.web.certification.CertificationSummaryResponse;
import com.kurekurecredential.web.common.ResourceNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CertificationService {

	private final CertificationRepository certificationRepository;

	public CertificationListResponse findAll(String keyword) {
		List<Certification> certifications;
		if (StringUtils.hasText(keyword)) {
			String normalizedKeyword = keyword.trim();
			certifications = certificationRepository
					.findByNameContainingIgnoreCaseOrProviderContainingIgnoreCaseOrderByNameAsc(
							normalizedKeyword,
							normalizedKeyword);
		} else {
			certifications = certificationRepository.findAllByOrderByNameAsc();
		}

		List<CertificationSummaryResponse> items = certifications.stream()
				.map(CertificationSummaryResponse::from)
				.toList();
		return CertificationListResponse.of(items);
	}

	public CertificationDetailResponse findById(Long certificationId) {
		Certification certification = certificationRepository.findById(certificationId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"資格が見つかりません。id=" + certificationId));
		return CertificationDetailResponse.from(certification);
	}
}
