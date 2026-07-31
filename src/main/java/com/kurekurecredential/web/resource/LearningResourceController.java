package com.kurekurecredential.web.resource;

import com.kurekurecredential.domain.certification.CurrentLevel;
import com.kurekurecredential.domain.study.ResourceType;
import com.kurekurecredential.service.resource.LearningResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/learning-resources")
public class LearningResourceController {

	private final LearningResourceService learningResourceService;

	@GetMapping
	public LearningResourceListResponse findAll(
			@RequestParam(required = false) Long certificationId,
			@RequestParam(required = false) ResourceType resourceType,
			@RequestParam(required = false) CurrentLevel targetLevel) {
		return learningResourceService.findAll(certificationId, resourceType, targetLevel);
	}

	@GetMapping("/{resourceId}")
	public LearningResourceDetailResponse findById(
			@PathVariable Long resourceId) {
		return learningResourceService.findById(resourceId);
	}
}
