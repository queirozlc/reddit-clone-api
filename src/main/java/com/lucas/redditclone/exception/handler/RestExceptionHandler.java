package com.lucas.redditclone.exception.handler;

import com.lucas.redditclone.exception.mail_sender.MailSenderException;
import com.lucas.redditclone.exception.mail_sender.MailSenderExceptionDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(MailSenderException.class)
	public ResponseEntity<MailSenderExceptionDetails> handleMailSenderException(MailSenderException e) {
		var exceptionDetails = MailSenderExceptionDetails
				.builder()
				.title("Mail Sender Exception. Check api documentation.")
				.details(e.getClass().getName())
				.status(INTERNAL_SERVER_ERROR.value())
				.message(e.getMessage())
				.timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.internalServerError().body(exceptionDetails);
	}

}
