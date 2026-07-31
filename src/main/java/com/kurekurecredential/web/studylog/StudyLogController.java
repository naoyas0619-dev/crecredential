package com.kurekurecredential.web.studylog;

import com.kurekurecredential.security.AuthUserDetails;
import com.kurekurecredential.service.studylog.StudyLogService;
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
public class StudyLogController {

	private final StudyLogService studyLogService;

	@PostMapping("/certification-goals/{goalId}/study-logs")
	@ResponseStatus(HttpStatus.CREATED)
	public StudyLogResponse create(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long goalId,
			@Valid @RequestBody StudyLogRequest request) {
		return studyLogService.create(userDetails.getId(), goalId, request);
	}

	@GetMapping("/study-logs")
	public StudyLogListResponse findAll(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@RequestParam(required = false) Long goalId,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate studiedFrom,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate studiedTo) {
		return studyLogService.findAll(
				userDetails.getId(),
				goalId,
				studiedFrom,
				studiedTo);
	}

	@GetMapping("/study-logs/{logId}")
	public StudyLogResponse findById(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long logId) {
		return studyLogService.findById(userDetails.getId(), logId);
	}

	@PutMapping("/study-logs/{logId}")
	public StudyLogResponse update(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long logId,
			@Valid @RequestBody StudyLogRequest request) {
		return studyLogService.update(userDetails.getId(), logId, request);
	}
}
