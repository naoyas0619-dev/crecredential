package com.kurekurecredential.security;

import com.kurekurecredential.domain.user.UserAccount;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthUserDetails implements UserDetails {

	private final UserAccount userAccount;

	public AuthUserDetails(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public Long getId() {
		return userAccount.getId();
	}

	public String getName() {
		return userAccount.getName();
	}

	public String getEmail() {
		return userAccount.getEmail();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public String getPassword() {
		return userAccount.getPasswordHash();
	}

	@Override
	public String getUsername() {
		return userAccount.getEmail();
	}
}
