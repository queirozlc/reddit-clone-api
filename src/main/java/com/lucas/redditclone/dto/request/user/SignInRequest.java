package com.lucas.redditclone.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInRequest {
    @NotBlank(message = "the field 'username' is required")
    @Size(min = 6, message = "the field 'username' must be at least {min} characters")
    @Pattern(regexp = "([A-Za-z0-9_](?:(?:[A-Za-z0-9_]|(?:\\.(?!\\.))){0,28}(?:[A-Za-z0-9_]))?)",
            message = "the field 'username' must be a valid username. Ex: name.lastname")
    private String username;
    @NotBlank(message = "the field 'password' is required")
    @Size(min = 6, max = 48, message = "the field 'password' must be at least {min} characters")
    private String password;
}
