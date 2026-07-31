package com.kurekurecredential.web.mockexam;

import com.kurekurecredential.security.AuthUserDetails;
import com.kurekurecredential.service.mockexam.MockExamResultService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api")
public class MockExamResultController {

	private final MockExamResultService mockExamResultService;

	@PostMapping("/certification-goals/{goalId}/mock-exam-results")
	@ResponseStatus(HttpStatus.CREATED)
	public MockExamResultResponse create(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long goalId,
			@Valid @RequestBody MockExamResultRequest request) {
		return mockExamResultService.create(userDetails.getId(), goalId, request);
	}

	@GetMapping("/mock-exam-results")
	public MockExamResultListResponse findAll(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@RequestParam(required = false) Long goalId,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate examFrom,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate examTo) {
		return mockExamResultService.findAll(
				userDetails.getId(),
				goalId,
				examFrom,
				examTo);
	}

	@GetMapping("/mock-exam-results/{resultId}")
	public MockExamResultResponse findById(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long resultId) {
		return mockExamResultService.findById(userDetails.getId(), resultId);
	}

	@PutMapping("/mock-exam-results/{resultId}")
	public MockExamResultResponse update(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long resultId,
			@Valid @RequestBody MockExamResultRequest request) {
		return mockExamResultService.update(
				userDetails.getId(),
				resultId,
				request);
	}
}
