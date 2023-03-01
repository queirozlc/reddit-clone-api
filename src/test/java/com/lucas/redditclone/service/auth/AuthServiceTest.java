package com.lucas.redditclone.service.auth;

import com.lucas.redditclone.dto.request.user.SignInRequest;
import com.lucas.redditclone.dto.request.user.UserRequest;
import com.lucas.redditclone.dto.response.MailResponseBody;
import com.lucas.redditclone.dto.response.SignInResponse;
import com.lucas.redditclone.entity.RefreshToken;
import com.lucas.redditclone.entity.Role;
import com.lucas.redditclone.entity.User;
import com.lucas.redditclone.entity.VerificationToken;
import com.lucas.redditclone.entity.enums.RoleName;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.mapper.UserMapper;
import com.lucas.redditclone.repository.RoleRepository;
import com.lucas.redditclone.repository.UserRepository;
import com.lucas.redditclone.repository.VerificationTokenRepository;
import com.lucas.redditclone.service.impl.EmailService;
import com.lucas.redditclone.service.jwt.JwtService;
import com.lucas.redditclone.service.refresh_token.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AuthServiceTest {
	public static final String EMAIL = "email";
	public static final String USERNAME = "@username";
	public static final String PASSWORD = "password";
	public static final String VERIFICATION_TOKEN = "b0a80121-7ac0-4e71-9c0a-03b600b4b0a1";
	public static final String NAME = "user";
	public static final String TOKEN = "token";
	public static final String ALREADY_EXISTS_A_USER_WITH_THIS_USERNAME = "Already exists a user with this username";
	public static final String ALREADY_EXISTS_A_USER_WITH_THIS_EMAIL = "Already exists a user with this email";
	public static final String TOKEN_INVALID = "Token invalid.";
	public static final String USER_NOT_FOUND = "User not found.";
	public static final String TOKEN_EXPIRED = "Token expired.";
	public static final String YOUR_ACCOUNT_IS_ALREADY_ENABLED = "Your account is already enabled.";
	public static final String ACCOUNT_ALREADY_ENABLED = YOUR_ACCOUNT_IS_ALREADY_ENABLED;
	public static final String VERIFICATION_TOKEN_STILL_VALID_CHECK_THE_EMAIL = "Your verification token still valid, check the email.";
	public static final String USERNAME_OR_PASSWORD_INCORRECT = "Username or password incorrect.";
	private static final UUID ID = UUID.fromString("c0a80121-7ac0-4e71-9c0a-03b600b4b0a2");
	private static final String PASSWORD_ENCODED = "passwordEncoded";
	private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000L;
	private static final String ACCOUNT_NOT_ENABLED = "Your account is not enabled. Please verify your account.";
	public static User user;
	public static Role role;
	public static Optional<User> userOptional;
	public static Optional<VerificationToken> verificationTokenOptional;
	public static UserRequest userRequest;
	public static VerificationToken verificationToken;
	public static SignInResponse signInResponse;
	public static Authentication authenticate;
	public static RefreshToken refreshToken;
	public static SignInRequest signInRequest;
	public static Optional<Role> roleOptional;

	@Spy
	@InjectMocks
	private AuthServiceImpl authService;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private JwtService jwtService;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private UserRepository userRepository;
	@Mock
	private VerificationTokenRepository verificationTokenRepository;
	@Mock
	private RefreshTokenService refreshTokenService;
	@Mock
	private UserMapper userMapper;
	@Mock
	private EmailService emailService;
	@Mock
	private RoleRepository roleRepository;
	@Mock
	private SecurityContext securityContext;

	@BeforeEach
	void setUp() {
		SecurityContextHolder.setContext(securityContext);
		initClasses();
	}

	@Test
	void createUserWhenSuccessfully() {
		when(userMapper.toUser(any(UserRequest.class))).thenReturn(user);

		when(userRepository.save(any(User.class))).thenReturn(user);

		when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);

		when(authService.generateVerificationToken(user)).thenReturn(verificationToken.getToken());

		when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD_ENCODED);

		doNothing().when(authService).sendVerificationEmail(any(User.class), anyString());

		authService.signUp(userRequest);

		verify(userRepository, times(1)).save(user);
		verify(passwordEncoder, times(1)).encode(PASSWORD);
		assertEquals(verificationToken.getToken(), authService.generateVerificationToken(user));
		assertEquals(String.class, authService.generateVerificationToken(user).getClass());
	}

	@Test
	void shouldNotCreateUserWhenUserAlreadyExistsByUsername() {
		when(userRepository.existsByUsername(anyString())).thenReturn(true);

		try {
			authService.signUp(userRequest);
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(ALREADY_EXISTS_A_USER_WITH_THIS_USERNAME, ex.getMessage());
		}
	}

	@Test
	void shouldNotCreateUserWhenUserAlreadyExistsByEmail() {
		when(userRepository.existsByEmail(anyString())).thenReturn(true);

		try {
			authService.signUp(userRequest);
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(ALREADY_EXISTS_A_USER_WITH_THIS_EMAIL, ex.getMessage());
		}
	}


	@Test
	void generateVerificationTokenWhenSuccessfully() {
		when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);

		String response = authService.generateVerificationToken(user);

		verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
		assertEquals(VERIFICATION_TOKEN, response);
		assertEquals(String.class, response.getClass());
		assertEquals(UUID.class, UUID.fromString(response).getClass());
	}

	@Test
	void sendVerificationEmailWhenSuccessfully() {
		doNothing().when(emailService).sendEmail(any(MailResponseBody.class));

		authService.sendVerificationEmail(user, verificationToken.getToken());

		verify(emailService, times(1)).sendEmail(any(MailResponseBody.class));
	}


	@Test
	void verifyAccountWhenSuccessfully() {
		when(verificationTokenRepository.findByToken(anyString())).thenReturn(verificationTokenOptional);
		when(userRepository.findByUsername(anyString())).thenReturn(userOptional);
		doNothing().when(authService).updateRegister(any(VerificationToken.class), any(User.class));

		authService.verifyAccount(VERIFICATION_TOKEN);


		verify(verificationTokenRepository, atMostOnce()).findById(ID);
		verify(userRepository, times(1)).findByUsername(USERNAME);
	}

	@Test
	void shouldThrowBadRequestExceptionWhenVerificationTokenNotFound() {
		when(verificationTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

		try {
			authService.verifyAccount(anyString());
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(TOKEN_INVALID, ex.getMessage());
			verify(verificationTokenRepository, times(1)).findByToken(anyString());
		}
	}

	@Test
	void shouldThrowBadRequestExceptionWhenUserNotFound() {
		when(verificationTokenRepository.findByToken(anyString())).thenReturn(verificationTokenOptional);
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

		try {
			authService.verifyAccount(anyString());
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(USER_NOT_FOUND, ex.getMessage());
			verify(verificationTokenRepository, times(1)).findByToken(anyString());
		}
	}

	@Test
	void shouldThrowBadRequestExceptionWhenVerificationTokenExpired() {
		when(verificationTokenRepository.findByToken(anyString())).thenReturn(verificationTokenOptional);
		when(userRepository.findByUsername(anyString())).thenReturn(userOptional);
		verificationToken.setExpirationDate(LocalDateTime.now().minusSeconds(10));

		try {
			authService.verifyAccount(VERIFICATION_TOKEN);
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(TOKEN_EXPIRED, ex.getMessage());
			verify(verificationTokenRepository, times(1)).findByToken(anyString());
			verify(userRepository, times(1)).findByUsername(anyString());
		}
	}

	@Test
	void shouldThrowBadRequestExceptionWhenUserAlreadyVerified() {
		when(verificationTokenRepository.findByToken(anyString())).thenReturn(verificationTokenOptional);
		when(userRepository.findByUsername(anyString())).thenReturn(userOptional);
		user.setEnabled(true);

		try {
			authService.verifyAccount(VERIFICATION_TOKEN);
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(ACCOUNT_ALREADY_ENABLED, ex.getMessage());
			verify(verificationTokenRepository, times(1)).findByToken(anyString());
			verify(userRepository, times(1)).findByUsername(anyString());
		}
	}

	@Test
	void refreshAccountWhenSuccessfully() {
		when(verificationTokenRepository.findByToken(anyString())).thenReturn(verificationTokenOptional);
		when(userRepository.findByUsername(anyString())).thenReturn(userOptional);
		when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);
		when(authService.generateVerificationToken(user)).thenReturn(verificationToken.getToken());
		doNothing().when(authService).sendVerificationEmail(any(User.class), anyString());

		verificationToken.setExpirationDate(LocalDateTime.now());

		authService.refreshAccount(VERIFICATION_TOKEN);

		verify(verificationTokenRepository, atMostOnce()).findById(ID);
		verify(userRepository, times(1)).findByUsername(USERNAME);
		verify(authService, times(1)).generateVerificationToken(user);
		verify(authService, times(1)).sendVerificationEmail(user, VERIFICATION_TOKEN);
	}

	@Test
	void refreshAccountShouldThrowBadRequestExceptionWhenVerificationTokenRemainsValid() {
		when(verificationTokenRepository.findByToken(anyString())).thenReturn(verificationTokenOptional);
		when(userRepository.findByUsername(anyString())).thenReturn(userOptional);

		try {
			authService.refreshAccount(VERIFICATION_TOKEN);
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(VERIFICATION_TOKEN_STILL_VALID_CHECK_THE_EMAIL, ex.getMessage());
			verify(verificationTokenRepository, times(1)).findByToken(anyString());
			verify(userRepository, times(1)).findByUsername(anyString());
			verify(authService, atMost(0)).generateVerificationToken(user);
		}
	}

	@Test
	void refreshAccountShouldThrowBadRequestExceptionWhenUserAlreadyVerified() {
		when(verificationTokenRepository.findByToken(anyString())).thenReturn(verificationTokenOptional);
		when(userRepository.findByUsername(anyString())).thenReturn(userOptional);
		verificationToken.setExpirationDate(LocalDateTime.now().minusSeconds(10));
		user.setEnabled(true);

		try {
			authService.refreshAccount(VERIFICATION_TOKEN);
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(YOUR_ACCOUNT_IS_ALREADY_ENABLED, ex.getMessage());
			verify(verificationTokenRepository, times(1)).findByToken(anyString());
			verify(userRepository, times(1)).findByUsername(anyString());
			verify(authService, atMost(0)).generateVerificationToken(user);
		}
	}

	@Test
	void signInWhenSuccessfully() {
		when(userRepository.findByUsername(anyString())).thenReturn(userOptional);
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authenticate);
		when(jwtService.generateToken(any(User.class))).thenReturn(TOKEN);
		when(refreshTokenService.generateRefreshToken(any(User.class), anyBoolean(), any(HttpServletResponse.class),
				any(HttpServletRequest.class))).thenReturn(refreshToken);

		user.setEnabled(true);
		SignInResponse response = authService.signIn(signInRequest, null, null);

		verify(userRepository, times(1)).findByUsername(USERNAME);
		verify(passwordEncoder, times(1)).matches(anyString(), anyString());
		verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
		verify(jwtService, times(1)).generateToken(user);
		verify(refreshTokenService, times(1)).generateRefreshToken(user, true, null, null);
		assertEquals(TOKEN, response.getToken());
		assertEquals(String.class, response.getToken().getClass());
		assertEquals(user.getUsername(), response.getUsername());

	}

	@Test
	void signInShouldThrowBadRequestExceptionWhenUserIsNotVerified() {
		when(userRepository.findByUsername(anyString())).thenReturn(userOptional);
		user.setEnabled(false);

		try {
			authService.signIn(signInRequest, null, null);
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(ACCOUNT_NOT_ENABLED, ex.getMessage());
			verify(userRepository, times(1)).findByUsername(USERNAME);
			verify(authenticationManager, atMost(0)).authenticate(any(Authentication.class));
			verify(jwtService, atMost(0)).generateToken(user);
			verify(refreshTokenService,
					atMost(0))
					.generateRefreshToken(user, true, null, null);
		}
	}

	@Test
	void signInShouldThrowBadRequestExceptionWhenPasswordDoesNotMatch() {
		when(userRepository.findByUsername(anyString())).thenReturn(userOptional);
		user.setEnabled(true);
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		try {
			authService.signIn(signInRequest, null, null);
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(USERNAME_OR_PASSWORD_INCORRECT, ex.getMessage());
			verify(userRepository, times(1)).findByUsername(USERNAME);
			verify(authenticationManager, atMost(0)).authenticate(any(Authentication.class));
			verify(jwtService, atMost(0)).generateToken(user);
			verify(refreshTokenService,
					atMost(0))
					.generateRefreshToken(user, true, null, null);
		}
	}

	@Test
	void getCurrentUserWhenSuccessfully() {
		when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authenticate);
		when(userRepository.findByUsername(anyString())).thenReturn(userOptional);


		var response = authService.getCurrentUser();

		verify(userRepository, times(1)).findByUsername(USERNAME);
		assertEquals(user, response);
		assertEquals(User.class, response.getClass());
		assertNotNull(response);
		assertEquals(user.getId(), response.getId());
		assertEquals(user.getName(), response.getName());
		assertEquals(user.getUsername(), response.getUsername());
	}

	@Test
	void updateRegisterWhenSuccessfully() {
		when(roleRepository.findByName(any(RoleName.class))).thenReturn(roleOptional);
		when(userRepository.save(any(User.class))).thenReturn(user);
		doNothing().when(verificationTokenRepository).delete(any(VerificationToken.class));

		authService.updateRegister(verificationToken, user);

		verify(userRepository, times(1)).save(user);
		verify(verificationTokenRepository, times(1)).delete(verificationToken);
	}

	private void initClasses() {
		role = Role.builder().id(ID).name(RoleName.ROLE_USER).build();

		user = User
				.builder()
				.id(ID)
				.name(NAME)
				.username(USERNAME)
				.email(EMAIL)
				.password(PASSWORD)
				.enabled(false)
				.createdAt(Instant.now())
				.role(role)
				.build();
		userOptional = Optional.of(user);
		userRequest = UserRequest
				.builder()
				.name(NAME)
				.username(USERNAME)
				.email(EMAIL)
				.password(PASSWORD)
				.enabled(false)
				.createdAt(Instant.now())
				.build();
		verificationToken = VerificationToken
				.builder()
				.id(ID)
				.token(VERIFICATION_TOKEN)
				.expirationDate(LocalDateTime.now().plusMinutes(10))
				.user(user)
				.build();
		verificationTokenOptional = Optional.of(verificationToken);
		signInResponse = SignInResponse
				.builder()
				.username(USERNAME)
				.token(TOKEN)
				.build();
		authenticate = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

		refreshToken = RefreshToken
				.builder()
				.id(ID)
				.token(TOKEN)
				.user(user)
				.expiredAt(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRATION_TIME))
				.createdAt(Instant.now())
				.build();

		signInRequest = SignInRequest
				.builder()
				.username(USERNAME)
				.password(PASSWORD)
				.rememberMe(true)
				.build();

		roleOptional = Optional.of(role);
	}
}