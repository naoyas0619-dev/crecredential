package com.kurekurecredential.service.auth;

import com.kurekurecredential.domain.user.UserAccount;
import com.kurekurecredential.repository.UserAccountRepository;
import com.kurekurecredential.security.AuthUserDetails;
import com.kurekurecredential.security.JwtService;
import com.kurekurecredential.web.auth.LoginRequest;
import com.kurekurecredential.web.auth.LoginResponse;
import com.kurekurecredential.web.auth.RegisterRequest;
import com.kurekurecredential.web.auth.UserResponse;
import com.kurekurecredential.web.common.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

	private final UserAccountRepository userAccountRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	@Transactional
	public UserResponse register(RegisterRequest request) {
		if (userAccountRepository.existsByEmail(request.email())) {
			throw new ConflictException("このメールアドレスは既に登録されています。");
		}

		UserAccount userAccount = new UserAccount();
		userAccount.setName(request.name());
		userAccount.setEmail(request.email());
		userAccount.setPasswordHash(passwordEncoder.encode(request.password()));

		UserAccount savedUser = userAccountRepository.save(userAccount);
		return toResponse(savedUser);
	}

	public LoginResponse login(LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password()));
		AuthUserDetails userDetails = (AuthUserDetails) authentication.getPrincipal();
		String token = jwtService.generateToken(userDetails);
		return LoginResponse.bearer(token, jwtService.getExpirationSeconds());
	}

	public UserResponse me(AuthUserDetails userDetails) {
		return new UserResponse(userDetails.getId(), userDetails.getName(), userDetails.getEmail());
	}

	private UserResponse toResponse(UserAccount userAccount) {
		return new UserResponse(userAccount.getId(), userAccount.getName(), userAccount.getEmail());
	}
}
