package com.lucas.redditclone.exception.mail_sender;

import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MailSenderException extends MailException {
	public MailSenderException(String msg) {
		super(msg);
	}
}
