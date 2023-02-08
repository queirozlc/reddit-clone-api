package com.lucas.redditclone.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
	@Size(min = 6, max = 48, message = "the field 'password' must be at least {min} characters")
	private String password;
	private boolean enabled;
	private Instant createdAt;
}
