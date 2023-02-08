package com.lucas.redditclone.exception;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Data
public abstract class ExceptionDetails {
	private String title;
	private String details;
	private int status;
	private String message;
	private LocalDateTime timestamp;
}
