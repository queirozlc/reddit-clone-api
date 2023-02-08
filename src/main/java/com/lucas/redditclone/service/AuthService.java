package com.lucas.redditclone.service;

import com.lucas.redditclone.dto.request.user.UserRequest;
import com.lucas.redditclone.entity.User;

public interface AuthService {

	void signUp(UserRequest userRequest);

	String generateVerificationToken(User user);

	void sendVerificationEmail(User user, String token);

	void verifyAccount(String token);

	void refreshAccount(String token);
}
