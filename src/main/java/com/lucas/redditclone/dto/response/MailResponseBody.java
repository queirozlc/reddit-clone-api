package com.lucas.redditclone.dto.response;

import com.lucas.redditclone.entity.enums.StatusEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailResponseBody {
	@NotBlank(message = "Owner id is required")
	private UUID ownerId;
	@NotBlank(message = "Email from sender is required")
	@Email(message = "Invalid email")
	private String emailFrom;
	@NotBlank(message = "Email of recipient is required")
	@Email(message = "Invalid email")
	private String emailTo;
	@NotBlank(message = "Subject is required")
	private String subject;
	@NotBlank(message = "Message is required")
	private String message;
	private LocalDateTime sendDateTime;
	private StatusEmail status;
}
