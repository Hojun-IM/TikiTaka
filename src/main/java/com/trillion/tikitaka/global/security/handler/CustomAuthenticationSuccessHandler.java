package com.trillion.tikitaka.global.security.handler;

import static com.trillion.tikitaka.global.security.constant.AuthenticationConstants.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.global.config.JwtConfig;
import com.trillion.tikitaka.global.response.ApiResponse;
import com.trillion.tikitaka.global.security.domain.CustomUserDetails;
import com.trillion.tikitaka.global.security.jwt.JwtTokenProvider;
import com.trillion.tikitaka.global.security.jwt.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtUtil jwtUtil;
	private final JwtConfig jwtConfig;
	private final JwtTokenProvider jwtTokenProvider;
	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException, ServletException {
		Long memberId = ((CustomUserDetails)authentication.getPrincipal()).getId();
		String username = authentication.getName();
		String role = authentication.getAuthorities().iterator().next().getAuthority();

		log.info("[JWT] 토큰 발급");
		String accessToken = jwtTokenProvider.createToken(
			memberId, username, role, jwtConfig.getAccessTokenExpirationInMS(), TOKEN_TYPE_ACCESS
		);
		String refreshToken = jwtTokenProvider.createToken(
			memberId, username, role, jwtConfig.getRefreshTokenExpirationInMS(), TOKEN_TYPE_REFRESH
		);

		jwtUtil.saveRefreshToken(username, refreshToken, jwtConfig.getRefreshTokenExpirationInMS());

		response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + accessToken);
		response.addHeader("Set-Cookie", jwtUtil.createRefreshTokenCookie(refreshToken).toString());

		boolean passwordChangeRequired = false;
		if (authentication.getPrincipal() instanceof CustomUserDetails) {
			Member member = ((CustomUserDetails)authentication.getPrincipal()).getMember();
			if (member.getLastPasswordChangedAt() == null
				|| member.getLastPasswordChangedAt().isBefore(LocalDateTime.now().minusDays(90))) {
				passwordChangeRequired = true;
			}
		}

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("id", memberId);
		responseData.put("role", role);
		responseData.put("passwordChangeRequired", passwordChangeRequired);

		ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success("로그인 되었습니다.", responseData);

		response.setStatus(HttpStatus.OK.value());
		response.setContentType(CONTENT_TYPE);
		response.setCharacterEncoding(ENCODING);
		String responseJson = objectMapper.writeValueAsString(apiResponse);
		response.getWriter().write(responseJson);
		log.info("[로그인 성공] : {}", username);
	}
}
