package com.lucas.redditclone.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.cors()
				.disable()
				.csrf()
				.disable()
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(r -> r
								.getRequestURL().toString().contains("/api/auth/**")).permitAll()
						.anyRequest().authenticated())
				.httpBasic().and()
				.build();
	}
}
