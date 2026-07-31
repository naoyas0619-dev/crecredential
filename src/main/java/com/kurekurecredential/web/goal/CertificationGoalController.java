package com.kurekurecredential.web.goal;

import com.kurekurecredential.domain.certification.GoalStatus;
import com.kurekurecredential.security.AuthUserDetails;
import com.kurekurecredential.service.goal.CertificationGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/certification-goals")
public class CertificationGoalController {

	private final CertificationGoalService certificationGoalService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CertificationGoalResponse create(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@Valid @RequestBody CreateCertificationGoalRequest request) {
		return certificationGoalService.create(userDetails.getId(), request);
	}

	@GetMapping
	public CertificationGoalListResponse findAll(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@RequestParam(required = false) GoalStatus status) {
		return certificationGoalService.findAll(userDetails.getId(), status);
	}

	@GetMapping("/{goalId}")
	public CertificationGoalResponse findById(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long goalId) {
		return certificationGoalService.findById(userDetails.getId(), goalId);
	}

	@PutMapping("/{goalId}")
	public CertificationGoalResponse update(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long goalId,
			@Valid @RequestBody UpdateCertificationGoalRequest request) {
		return certificationGoalService.update(userDetails.getId(), goalId, request);
	}
}
