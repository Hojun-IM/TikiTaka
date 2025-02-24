package com.trillion.tikitaka.global.security.jwt;

import static com.trillion.tikitaka.global.security.constant.AuthenticationConstants.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trillion.tikitaka.global.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JwtController {

	private final JwtService jwtService;
	private final JwtUtil jwtUtil;

	@PostMapping("/api/reissue")
	public ApiResponse<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
		JwtTokenResponse newTokens = jwtService.reissueTokens(request);

		response.setHeader(TOKEN_HEADER, TOKEN_PREFIX + newTokens.getAccessToken());
		response.addHeader("Set-Cookie", jwtUtil.createRefreshTokenCookie(newTokens.getRefreshToken()).toString());
		return ApiResponse.success("토큰이 재발급되었습니다.", null);
	}
}
