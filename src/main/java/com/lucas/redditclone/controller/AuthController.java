package com.lucas.redditclone.controller;

import com.lucas.redditclone.dto.request.refresh_token.RefreshTokenRequestBody;
import com.lucas.redditclone.dto.request.user.SignInRequest;
import com.lucas.redditclone.dto.request.user.UserRequest;
import com.lucas.redditclone.dto.response.SignInResponse;
import com.lucas.redditclone.dto.response.refresh_token.RefreshTokenResponseBody;
import com.lucas.redditclone.entity.RefreshToken;
import com.lucas.redditclone.service.auth.AuthService;
import com.lucas.redditclone.service.refresh_token.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Value("${cookies.key.cookie-name}")
    private String key;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody @Valid UserRequest userRequest) {
        authService.signUp(userRequest);
        return ResponseEntity.ok()
                .body("User created successfully, check the mail box to activate your account.");
    }

    @PostMapping("/login")
    public ResponseEntity<SignInResponse> login(@RequestBody @Valid SignInRequest signInRequest,
                                                @CookieValue(name = "reddit-session", defaultValue = "")
                                                String refreshTokenCookie) {
        SignInResponse signInResponse = authService.signIn(signInRequest);
        var refreshToken = refreshTokenService.generateRefreshToken(authService.getCurrentUser(), refreshTokenCookie);
        var cookie = createCookie(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(signInResponse);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        refreshTokenService.logout();
        var cookieCleaned = cleanCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieCleaned.toString())
                .body("User logged out successfully");
    }


    @PostMapping("/refresh/token")
    public ResponseEntity<RefreshTokenResponseBody> refreshAccessToken(@RequestBody @Valid
                                                                       RefreshTokenRequestBody refreshTokenRequestBody,
                                                                       @CookieValue(name = "reddit-session", defaultValue = "")
                                                                       String refreshTokenCookie) {
        refreshTokenRequestBody.setRefreshToken(refreshTokenCookie);
        var refreshTokenResponseBody = refreshTokenService.refreshAccessToken(refreshTokenRequestBody);
        var cookie = createCookie(refreshTokenResponseBody.getRefreshTokenEntity());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(refreshTokenResponseBody);
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

    @NotNull
    private ResponseCookie createCookie(RefreshToken refreshToken) {
        return ResponseCookie.from(key, refreshToken.getToken())
                .secure(false)
                .maxAge(604800)
                .domain("localhost")
                .path("/")
                .httpOnly(true)
                .build();
    }

    @NotNull
    private ResponseCookie cleanCookie() {
        return ResponseCookie.from(key, "").build();
    }
}
