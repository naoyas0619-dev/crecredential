package com.kurekurecredential.repository;

import com.kurekurecredential.domain.study.LearningResource;
import com.kurekurecredential.domain.study.ResourceType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {

	List<LearningResource> findByCertificationId(Long certificationId);

	List<LearningResource> findByCertificationIdAndResourceType(Long certificationId, ResourceType resourceType);
}
