package com.trillion.tikitaka.global.security.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.trillion.tikitaka.global.exception.ErrorCode;
import com.trillion.tikitaka.global.security.util.SecurityResponseUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

	private final SecurityResponseUtils responseUtils;

	@Override
	public void commence(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException
	) throws IOException {
		log.error("[인증] 인증되지 않은 사용자입니다.");
		ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
		Object errorCodeAttr = request.getAttribute("JWT_ERROR_CODE");
		if (errorCodeAttr instanceof ErrorCode ec) {
			errorCode = ec;
		}

		responseUtils.sendErrorResponse(response, errorCode);
	}
}
