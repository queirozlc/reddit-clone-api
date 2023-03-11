package com.lucas.redditclone.service.auth;

import com.lucas.redditclone.dto.request.user.SignInRequest;
import com.lucas.redditclone.dto.request.user.UserRequest;
import com.lucas.redditclone.dto.response.SignInResponse;
import com.lucas.redditclone.entity.User;

public interface AuthService {

    void signUp(UserRequest userRequest);

    String generateVerificationToken(User user);

    void sendVerificationEmail(User user, String token);

    void verifyAccount(String token);

    void refreshAccount(String token);

    SignInResponse signIn(SignInRequest signInRequest);

    User getCurrentUser();

}
