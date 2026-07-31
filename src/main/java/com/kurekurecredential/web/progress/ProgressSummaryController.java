package com.kurekurecredential.web.progress;

import com.kurekurecredential.security.AuthUserDetails;
import com.kurekurecredential.service.progress.ProgressSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/certification-goals")
public class ProgressSummaryController {

	private final ProgressSummaryService progressSummaryService;

	@GetMapping("/{goalId}/summary")
	public ProgressSummaryResponse getSummary(
			@AuthenticationPrincipal AuthUserDetails userDetails,
			@PathVariable Long goalId) {
		return progressSummaryService.getSummary(userDetails.getId(), goalId);
	}
}
