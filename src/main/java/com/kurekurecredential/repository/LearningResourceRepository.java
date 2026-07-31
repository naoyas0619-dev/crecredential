package com.kurekurecredential.repository;

import com.kurekurecredential.domain.study.LearningResource;
import com.kurekurecredential.domain.study.ResourceType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {

	@Query("""
			select resource
			from LearningResource resource
			join fetch resource.certification certification
			where (:certificationId is null or certification.id = :certificationId)
			  and (:resourceType is null or resource.resourceType = :resourceType)
			  and (:targetLevel is null or resource.targetLevel = :targetLevel)
			order by resource.recommendationScore desc, resource.title asc
			""")
	List<LearningResource> search(
			@Param("certificationId") Long certificationId,
			@Param("resourceType") ResourceType resourceType,
			@Param("targetLevel") String targetLevel);

	@Override
	@EntityGraph(attributePaths = "certification")
	Optional<LearningResource> findById(Long id);
}
