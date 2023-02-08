package com.lucas.redditclone.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
	@NotBlank(message = "the field 'name' is required")
	private String name;
	@NotBlank(message = "the field 'username' is required")
	private String username;
	@NotBlank(message = "the field 'email' is required")
	@Email(message = "the field 'email' must be a valid email")
	private String email;
	@NotBlank(message = "the field 'password' is required")
	@Min(value = 6, message = "the field 'password' must be at least 6 characters")
	@Max(value = 30, message = "the field 'password' must be at most 30 characters")
	private String password;
	private boolean enabled;
	private Instant createdAt;
}
