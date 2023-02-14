package com.lucas.redditclone.util.cookie;

import com.lucas.redditclone.exception.not_found.NotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {

	public static void createCookie(HttpServletResponse response,
	                                String key,
	                                String value,
	                                String domain,
	                                int maxAge,
	                                boolean secure) {
		var cookie = new Cookie(key, value);
		cookie.setHttpOnly(true);
		cookie.setSecure(secure);
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	public static void cleanCookie(HttpServletResponse response, String key) {
		var cookie = new Cookie(key, null);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	public static String getCookie(HttpServletRequest request, String key) {
		return Optional.of(request.getCookies())
				.flatMap(cookies ->
						Arrays
								.stream(cookies)
								.filter(cookie -> key.equals(cookie.getName()))
								.findAny()
								.map(Cookie::getValue))
				.orElseThrow(() -> new NotFoundException("Cookie not found"));
	}
}
