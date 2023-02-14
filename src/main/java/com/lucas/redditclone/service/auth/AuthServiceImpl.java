package com.lucas.redditclone.service.auth;

import com.lucas.redditclone.dto.request.user.SignInRequest;
import com.lucas.redditclone.dto.request.user.UserRequest;
import com.lucas.redditclone.dto.response.MailResponseBody;
import com.lucas.redditclone.dto.response.SignInResponse;
import com.lucas.redditclone.entity.Role;
import com.lucas.redditclone.entity.User;
import com.lucas.redditclone.entity.VerificationToken;
import com.lucas.redditclone.entity.enums.RoleName;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.exception.not_found.NotFoundException;
import com.lucas.redditclone.mapper.UserMapper;
import com.lucas.redditclone.repository.RoleRepository;
import com.lucas.redditclone.repository.UserRepository;
import com.lucas.redditclone.repository.VerificationTokenRepository;
import com.lucas.redditclone.service.impl.EmailService;
import com.lucas.redditclone.service.jwt.JwtService;
import com.lucas.redditclone.service.refresh_token.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
	private static final String USER_NOT_FOUND = "User not found.";
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final UserMapper userMapper;
	private final EmailService emailService;
	private final RoleRepository roleRepository;
	private final RefreshTokenService refreshTokenService;
	@Value("${spring.mail.username}")
	private String EMAIL_FROM;

	@Override
	public void signUp(UserRequest userRequest) {
		validateRequest(userRequest);
		User userToBeSaved = userMapper.toUser(userRequest);
		userToBeSaved.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		userToBeSaved.setCreatedAt(Instant.now());
		userToBeSaved.setEnabled(false);
		User userSaved = userRepository.save(userToBeSaved);
		String token = generateVerificationToken(userSaved);
		sendVerificationEmail(userSaved, token);
	}


	@Override
	public String generateVerificationToken(User user) {
		String token = UUID.randomUUID().toString();
		var verificationToken = VerificationToken.builder()
				.token(token)
				.user(user)
				.expirationDate(LocalDateTime.now().plusMinutes(15))
				.build();
		VerificationToken savedVerificationToken = verificationTokenRepository.save(verificationToken);
		return savedVerificationToken.getToken();
	}

	@Override
	public void sendVerificationEmail(User user, String verificationToken) {
		var verificationUrl = "http://localhost:8080/auth/verify/" + verificationToken;
		var subject = "Reddit Clone - Verify your account";
		var message = "Please click on the link below to verify your account:\n" + verificationUrl;
		var mailResponseBody = MailResponseBody
				.builder()
				.emailTo(user.getEmail())
				.emailFrom(EMAIL_FROM)
				.subject(subject)
				.ownerId(user.getId())
				.message(message)
				.build();
		emailService.sendEmail(mailResponseBody);
	}

	@Override
	public void verifyAccount(String token) {
		var verificationToken = verificationTokenRepository
				.findByToken(token)
				.orElseThrow(() -> new BadRequestException("Token invalid."));
		var user = userRepository.findByUsername(verificationToken.getUser().getUsername())
				.orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));

		if (verificationToken.getExpirationDate().isBefore(LocalDateTime.now())) {
			throw new BadRequestException("Token expired.");
		}

		if (verificationToken.getUser().isEnabled()) {
			throw new BadRequestException("Your account is already enabled.");
		}

		updateRegister(verificationToken, user);
	}

	@Override
	public void refreshAccount(String token) {
		var verificationToken = verificationTokenRepository
				.findByToken(token)
				.orElseThrow(() -> new BadRequestException("Token invalid."));
		var user = userRepository.findByUsername(verificationToken.getUser().getUsername())
				.orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));

		if (verificationToken.getExpirationDate().isAfter(LocalDateTime.now())) {
			throw new BadRequestException("Your verification token still valid, check the email.");
		}

		if (verificationToken.getUser().isEnabled()) {
			throw new BadRequestException("Your account is already enabled.");
		}

		String tokenRefresh = generateVerificationToken(user);
		sendVerificationEmail(user, tokenRefresh);
	}

	@Override
	public SignInResponse signIn(SignInRequest signInRequest, HttpServletResponse response,
	                             HttpServletRequest request) {
		var userToAuthenticate = userRepository
				.findByUsername(signInRequest.getUsername())
				.orElseThrow(() -> new BadRequestException("Username or password incorrect."));

		if (!userToAuthenticate.isEnabled())
			throw new BadRequestException("Your account is not enabled. Please verify your account.");

		if (!passwordEncoder.matches(signInRequest.getPassword(), userToAuthenticate.getPassword()))
			throw new BadRequestException("Username or password incorrect.");

		Authentication authenticate = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(),
						signInRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authenticate);

		var userAuthenticated = (User) authenticate.getPrincipal();

		String token = jwtService.generateToken(userAuthenticated);

		refreshTokenService.generateRefreshToken(
				userAuthenticated,
				signInRequest.isRememberMe(),
				response,
				request);

		return SignInResponse
				.builder()
				.token(token)
				.username(userToAuthenticate.getUsername())
				.build();
	}

	@Override
	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.findByUsername(((User) authentication.getPrincipal()).getUsername())
				.orElseThrow(() -> new BadRequestException("User not found"));
	}

	private void updateRegister(VerificationToken verificationToken, User user) {
		Role roleUser = roleRepository.findByName(RoleName.ROLE_USER)
				.orElseThrow(() -> new NotFoundException("Role not found."));
		user.setEnabled(true);
		user.setRole(roleUser);
		userRepository.save(user);
		verificationTokenRepository.delete(verificationToken);
	}

	private void validateRequest(UserRequest userRequest) {
		if (userRepository.existsByUsername(userRequest.getUsername())) {
			throw new BadRequestException("Already exists a user with this username");
		}

		if (userRepository.existsByEmail(userRequest.getEmail())) {
			throw new BadRequestException("Already exists a user with this email");
		}
	}

}
