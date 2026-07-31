package com.kurekurecredential.repository;

import com.kurekurecredential.domain.certification.Certification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

	List<Certification> findAllByOrderByNameAsc();

	List<Certification> findByNameContainingIgnoreCaseOrProviderContainingIgnoreCaseOrderByNameAsc(
			String name,
			String provider);
}
