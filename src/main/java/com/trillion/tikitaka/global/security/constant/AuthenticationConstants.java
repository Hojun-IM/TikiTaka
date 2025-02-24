package com.trillion.tikitaka.global.security.constant;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class AuthenticationConstants {

	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String CONTENT_TYPE = "application/json";
	public static final String ENCODING = "UTF-8";
	public static final String SPRING_SECURITY_USERNAME_KEY = "username";
	public static final String SPRING_SECURITY_PASSWORD_KEY = "password";

	public static final String TOKEN_TYPE_ACCESS = "access";
	public static final String TOKEN_TYPE_REFRESH = "refresh";

	public static final String LOGIN_PATH = "/login";
	public static final String LOGIN_HTTP_METHOD = "POST";
	public static final String LOGOUT_PATH = "/logout";
	public static final String LOGOUT_HTTP_METHOD = "POST";
	public static final long MAX_LOGIN_FAILURE_COUNT = 5;
}
