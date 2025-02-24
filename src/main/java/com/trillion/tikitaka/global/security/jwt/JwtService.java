package com.trillion.tikitaka.global.security.jwt;

import static com.trillion.tikitaka.global.security.constant.AuthenticationConstants.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trillion.tikitaka.global.config.JwtConfig;
import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

	private final JwtUtil jwtUtil;
	private final JwtConfig jwtConfig;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtTokenRepository jwtTokenRepository;

	@Transactional
	public JwtTokenResponse reissueTokens(HttpServletRequest request) {
		log.info("[토큰 재발급 요청]");
		String refreshToken = extractRefreshToken(request);
		if (refreshToken == null) {
			log.error("[토큰 재발급 요청] 리프레시 토큰이 존재하지 않습니다.");
			throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
		}

		validateRefreshToken(refreshToken);

		Boolean isRefreshTokenExist = jwtTokenRepository.existsByRefreshToken(refreshToken);
		if (!isRefreshTokenExist) {
			log.error("[토큰 재발급 요청] 리프레시 토큰이 존재하지 않습니다.");
			throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
		}

		Long userId = jwtTokenProvider.getMemberId(refreshToken);
		String username = jwtTokenProvider.getUsername(refreshToken);
		String role = jwtTokenProvider.getRole(refreshToken);

		String newAccessToken = jwtTokenProvider.createToken(
			userId, username, role, jwtConfig.getAccessTokenExpirationInMS(), TOKEN_TYPE_ACCESS
		);
		String newRefreshToken = jwtTokenProvider.createToken(
			userId, username, role, jwtConfig.getRefreshTokenExpirationInMS(), TOKEN_TYPE_REFRESH
		);

		jwtTokenRepository.deleteByRefreshToken(refreshToken);
		jwtUtil.saveRefreshToken(username, newRefreshToken, jwtConfig.getRefreshTokenExpirationInMS());

		log.info("[토큰 재발급 요청] 토큰 재발급 완료");
		return new JwtTokenResponse(newAccessToken, newRefreshToken);
	}

	private String extractRefreshToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(TOKEN_TYPE_REFRESH)) {
				return cookie.getValue();
			}
		}

		return null;
	}

	private void validateRefreshToken(String refreshToken) {
		try {
			jwtUtil.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			log.error("[토큰 재발급 요청] 리프레시 토큰이 만료되었습니다.");
			throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
		}

		String type = jwtTokenProvider.getType(refreshToken);
		if (!type.equals(TOKEN_TYPE_REFRESH)) {
			log.error("[토큰 재발급 요청] 잘못된 리프레시 토큰압니다.");
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}
	}
}
