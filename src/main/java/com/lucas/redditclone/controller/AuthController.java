package com.lucas.redditclone.controller;

import com.lucas.redditclone.dto.request.user.UserRequest;
import com.lucas.redditclone.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<String> signUp(@RequestBody @Valid UserRequest userRequest) {
		authService.signUp(userRequest);
		return ResponseEntity.ok()
				.body("User created successfully, check the mail box to activate your account.");
	}

	@GetMapping("/verify/{token}")
	public ResponseEntity<String> verifyAccount(@PathVariable String token) {
		authService.verifyAccount(token);
		return ResponseEntity.ok()
				.body("Your account has been verified successfully.");
	}

}
