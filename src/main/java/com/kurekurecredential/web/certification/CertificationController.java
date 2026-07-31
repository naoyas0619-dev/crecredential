package com.kurekurecredential.web.certification;

import com.kurekurecredential.service.certification.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/certifications")
public class CertificationController {

	private final CertificationService certificationService;

	@GetMapping
	public CertificationListResponse findAll(
			@RequestParam(required = false) String keyword) {
		return certificationService.findAll(keyword);
	}

	@GetMapping("/{certificationId}")
	public CertificationDetailResponse findById(
			@PathVariable Long certificationId) {
		return certificationService.findById(certificationId);
	}
}
