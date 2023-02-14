package com.lucas.redditclone.exception.handler;

import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.exception.bad_request.BadRequestExceptionDetails;
import com.lucas.redditclone.exception.mail_sender.MailSenderException;
import com.lucas.redditclone.exception.mail_sender.MailSenderExceptionDetails;
import com.lucas.redditclone.exception.not_found.NotFoundException;
import com.lucas.redditclone.exception.not_found.NotFoundExceptionDetails;
import com.lucas.redditclone.exception.unauthorized.UnauthorizedException;
import com.lucas.redditclone.exception.unauthorized.UnauthorizedExceptionDetails;
import com.lucas.redditclone.exception.validation.ValidationExceptionDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

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

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
	                                                              HttpHeaders headers,
	                                                              HttpStatusCode status, WebRequest request) {
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

		String fields = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));
		String message = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));

		var exceptionDetails = ValidationExceptionDetails
				.builder()
				.title("Validation Error, check the api documentation.")
				.details(ex.getClass().getName())
				.status(status.value())
				.message(message)
				.timestamp(LocalDateTime.now())
				.fields(fields)
				.build();

		return ResponseEntity.badRequest().body(exceptionDetails);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<NotFoundExceptionDetails> handleNotFoundException(NotFoundException e) {
		var exceptionDetails = NotFoundExceptionDetails
				.builder()
				.title("Not Found Exception. Check api documentation.")
				.details(e.getClass().getName())
				.status(NOT_FOUND.value())
				.message(e.getMessage())
				.timestamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(exceptionDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<BadRequestExceptionDetails> handleBadRequestException(BadRequestException e) {
		var exceptionDetails = BadRequestExceptionDetails
				.builder()
				.title("Bad Request Exception. Check api documentation.")
				.details(e.getClass().getName())
				.status(BAD_REQUEST.value())
				.message(e.getMessage())
				.timestamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(exceptionDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<UnauthorizedExceptionDetails> handleUnauthorizedException(UnauthorizedException e) {
		var exceptionDetails = UnauthorizedExceptionDetails
				.builder()
				.title("Unauthorized Exception. Check api documentation.")
				.details(e.getClass().getName())
				.status(UNAUTHORIZED.value())
				.message(e.getMessage())
				.timestamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(exceptionDetails, HttpStatus.UNAUTHORIZED);
	}
}
