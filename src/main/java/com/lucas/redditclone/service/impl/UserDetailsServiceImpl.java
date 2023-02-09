package com.lucas.redditclone.service.impl;


import com.lucas.redditclone.exception.not_found.NotFoundException;
import com.lucas.redditclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return repository
				.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("User not found"));
	}
}
