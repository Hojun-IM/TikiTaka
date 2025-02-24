package com.trillion.tikitaka.global.security.filter;

import static com.trillion.tikitaka.global.security.constant.AuthenticationConstants.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
		new AntPathRequestMatcher(LOGIN_PATH, LOGIN_HTTP_METHOD);

	private final ObjectMapper objectMapper;

	public CustomAuthenticationFilter(ObjectMapper objectMapper) {
		super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
		this.objectMapper = objectMapper;
	}

	@Override
	public Authentication attemptAuthentication(
		HttpServletRequest request,
		HttpServletResponse response
	) throws AuthenticationException, IOException {

		if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
			throw new AuthenticationServiceException("로그인 요청을 불러오는 중 오류가 발생했습니다.");
		}

		String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

		Map<String, String> loginAttempt = objectMapper.readValue(messageBody, Map.class);
		String username = loginAttempt.get(SPRING_SECURITY_USERNAME_KEY);
		String password = loginAttempt.get(SPRING_SECURITY_PASSWORD_KEY);
		log.info("[로그인 요청] : {}", username);

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
		return this.getAuthenticationManager().authenticate(authToken);
	}
}
