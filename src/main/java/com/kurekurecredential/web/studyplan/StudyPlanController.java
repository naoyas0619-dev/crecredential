package com.kurekurecredential.web.studyplan;

import com.kurekurecredential.security.AuthUserDetails;
import com.kurekurecredential.service.studyplan.StudyPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class StudyPlanController {

	private final StudyPlanService studyPlanService;

	@PostMapping("/certification-goals/{goalId}/study-plans")
	@ResponseStatus(HttpStatus.CREATED)
	public StudyPlanResponse create(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long goalId,
			@Valid @RequestBody CreateStudyPlanRequest request) {
		return studyPlanService.create(userDetails.getId(), goalId, request);
	}

	@GetMapping("/certification-goals/{goalId}/study-plans")
	public StudyPlanListResponse findAll(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long goalId) {
		return studyPlanService.findAll(userDetails.getId(), goalId);
	}

	@GetMapping("/study-plans/{studyPlanId}")
	public StudyPlanResponse findById(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long studyPlanId) {
		return studyPlanService.findById(userDetails.getId(), studyPlanId);
	}
}
