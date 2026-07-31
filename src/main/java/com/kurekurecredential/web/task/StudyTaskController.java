package com.kurekurecredential.web.task;

import com.kurekurecredential.domain.study.TaskStatus;
import com.kurekurecredential.security.AuthUserDetails;
import com.kurekurecredential.service.task.StudyTaskService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
public class StudyTaskController {

	private final StudyTaskService studyTaskService;

	@PostMapping("/certification-goals/{goalId}/study-tasks")
	@ResponseStatus(HttpStatus.CREATED)
	public StudyTaskResponse create(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long goalId,
			@Valid @RequestBody CreateStudyTaskRequest request) {
		return studyTaskService.create(userDetails.getId(), goalId, request);
	}

	@GetMapping("/study-tasks")
	public StudyTaskListResponse findAll(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@RequestParam(required = false) Long goalId,
			@RequestParam(required = false) TaskStatus status,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueFrom,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueTo) {
		return studyTaskService.findAll(
				userDetails.getId(),
				goalId,
				status,
				dueFrom,
				dueTo);
	}

	@GetMapping("/study-tasks/{taskId}")
	public StudyTaskResponse findById(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long taskId) {
		return studyTaskService.findById(userDetails.getId(), taskId);
	}

	@PutMapping("/study-tasks/{taskId}")
	public StudyTaskResponse update(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long taskId,
			@Valid @RequestBody UpdateStudyTaskRequest request) {
		return studyTaskService.update(userDetails.getId(), taskId, request);
	}

	@PatchMapping("/study-tasks/{taskId}/complete")
	public StudyTaskResponse complete(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long taskId) {
		return studyTaskService.complete(userDetails.getId(), taskId);
	}
}
