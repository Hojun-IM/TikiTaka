package com.trillion.tikitaka.global.security.handler;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
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
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final SecurityResponseUtils responseUtils;

	@Override
	public void handle(
		HttpServletRequest request,
		HttpServletResponse response,
		AccessDeniedException accessDeniedException
	) throws IOException {
		log.error("[인증] 접근 권한이 없습니다.");
		ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
		responseUtils.sendErrorResponse(response, errorCode);
	}
}
