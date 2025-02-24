package com.trillion.tikitaka.global.security.util;

import static com.trillion.tikitaka.global.security.constant.AuthenticationConstants.*;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trillion.tikitaka.global.exception.ErrorCode;
import com.trillion.tikitaka.global.response.ErrorResponse;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityResponseUtils {

	private final ObjectMapper objectMapper = new ObjectMapper();

	public void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		ErrorResponse errorResponse = new ErrorResponse(
			errorCode.getHttpStatus(),
			errorCode.getErrorCode(),
			errorCode.getMessage()
		);
		response.setContentType(CONTENT_TYPE);
		response.setStatus(errorCode.getHttpStatus().value());
		response.setCharacterEncoding(ENCODING);

		String responseBody = objectMapper.writeValueAsString(errorResponse);
		response.getWriter().write(responseBody);
	}
}
