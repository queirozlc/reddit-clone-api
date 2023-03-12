package com.lucas.redditclone.dto.request.refresh_token;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequestBody {
    @NotNull(message = "User id is required.")
    private UUID userId;
    private String refreshToken;
}
