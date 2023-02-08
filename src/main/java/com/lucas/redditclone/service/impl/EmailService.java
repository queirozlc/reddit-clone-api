package com.lucas.redditclone.service.impl;

import com.lucas.redditclone.dto.response.MailResponseBody;
import com.lucas.redditclone.entity.enums.StatusEmail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender mailSender;

	@Async
	public void sendEmail(@Valid MailResponseBody mailResponseBody) {
		mailResponseBody.setSendDateTime(LocalDateTime.now());
		try {
			var mailMessage = new SimpleMailMessage();
			mailMessage.setFrom(mailResponseBody.getEmailFrom());
			mailMessage.setTo(mailResponseBody.getEmailTo());
			mailMessage.setSubject(mailResponseBody.getSubject());
			mailMessage.setText(mailResponseBody.getMessage());
			mailSender.send(mailMessage);
			mailResponseBody.setStatus(StatusEmail.SENT);
		}
		catch (MailException e) {
			mailResponseBody.setStatus(StatusEmail.ERROR);
			e.printStackTrace();
		}
	}
}
