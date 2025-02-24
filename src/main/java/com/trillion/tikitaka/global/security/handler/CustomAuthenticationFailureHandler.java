package com.trillion.tikitaka.global.security.handler;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.trillion.tikitaka.global.exception.ErrorCode;
import com.trillion.tikitaka.global.security.util.SecurityResponseUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final SecurityResponseUtils responseUtils;

	@Override
	public void onAuthenticationFailure(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException
	) throws IOException {
		ErrorCode errorCode;

		if (authException instanceof BadCredentialsException) {
			log.error("[인증] 잘못된 사용자 이름 또는 비밀번호입니다.");
			errorCode = ErrorCode.INVALID_USERNAME_OR_PASSWORD;
		} else if (authException instanceof LockedException) {
			log.error("[인증] 계정이 잠겼습니다.");
			errorCode = ErrorCode.ACCOUNT_LOCKED;
		} else {
			log.error("[인증] 인증 실패");
			errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		}

		responseUtils.sendErrorResponse(response, errorCode);
	}
}
