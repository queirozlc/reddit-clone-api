package com.lucas.redditclone.service.impl;

import com.lucas.redditclone.dto.request.user.UserRequest;
import com.lucas.redditclone.dto.response.MailResponseBody;
import com.lucas.redditclone.entity.User;
import com.lucas.redditclone.entity.VerificationToken;
import com.lucas.redditclone.mapper.UserMapper;
import com.lucas.redditclone.repository.UserRepository;
import com.lucas.redditclone.repository.VerificationTokenRepository;
import com.lucas.redditclone.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
	@Value("${spring.mail.username}")
	private String EMAIL_FROM;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final UserMapper userMapper;
	private final EmailService emailService;

	@Override
	public void signUp(UserRequest userRequest) {
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
		String verificationUrl = "http://localhost:8080/auth/verify/" + verificationToken;
		String subject = "Reddit Clone - Verify your account";
		String message = "Please click on the link below to verify your account:\n" + verificationUrl;
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
		VerificationToken verificationToken = verificationTokenRepository
				.findByToken(token)
				.orElseThrow(() -> new RuntimeException("Token invalid."));

		if (verificationToken.getExpirationDate().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Token expired.");
		}
		
	}
}
