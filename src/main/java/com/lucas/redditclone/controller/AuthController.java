package com.lucas.redditclone.controller;

import com.lucas.redditclone.dto.request.refresh_token.RefreshTokenRequestBody;
import com.lucas.redditclone.dto.request.user.SignInRequest;
import com.lucas.redditclone.dto.request.user.UserRequest;
import com.lucas.redditclone.dto.response.SignInResponse;
import com.lucas.redditclone.dto.response.refresh_token.RefreshTokenResponseBody;
import com.lucas.redditclone.service.auth.AuthService;
import com.lucas.redditclone.service.refresh_token.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;

	@PostMapping("/signup")
	public ResponseEntity<String> signUp(@RequestBody @Valid UserRequest userRequest) {
		authService.signUp(userRequest);
		return ResponseEntity.ok()
				.body("User created successfully, check the mail box to activate your account.");
	}

	@PostMapping("/login")
	public ResponseEntity<SignInResponse> login(@RequestBody @Valid SignInRequest signInRequest,
	                                            HttpServletResponse response) {
		return ResponseEntity.ok(authService.signIn(signInRequest, response));
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		refreshTokenService.logout(request, response);
		return ResponseEntity.ok().body("User logged out successfully");
	}

	@PostMapping("/refresh/token")
	public ResponseEntity<RefreshTokenResponseBody> refreshAccessToken(@RequestBody @Valid
	                                                                   RefreshTokenRequestBody refreshTokenRequestBody,
	                                                                   HttpServletRequest request,
	                                                                   HttpServletResponse response) {

		return ResponseEntity.ok(refreshTokenService.refreshAccessToken(refreshTokenRequestBody, request, response));
	}

	@GetMapping("/verify/{token}")
	public ResponseEntity<String> verifyAccount(@PathVariable String token) {
		authService.verifyAccount(token);
		return ResponseEntity.ok()
				.body("Your account has been verified successfully.");
	}

	@GetMapping("/verify/refresh/{token}")
	public ResponseEntity<String> refreshAccount(@PathVariable String token) {
		authService.refreshAccount(token);
		return ResponseEntity.ok()
				.body("We send you another email to verify your account. Please check your inbox.");
	}
}
