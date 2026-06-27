package com.kurekurecredential.repository;

import com.kurekurecredential.domain.user.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

	Optional<UserAccount> findByEmail(String email);

	boolean existsByEmail(String email);
}
