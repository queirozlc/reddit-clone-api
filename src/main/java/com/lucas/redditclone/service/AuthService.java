package com.lucas.redditclone.service;

import com.lucas.redditclone.entity.User;
import com.lucas.redditclone.request.user.UserRequest;

public interface AuthService {

	User signUp(UserRequest userRequest);

}
