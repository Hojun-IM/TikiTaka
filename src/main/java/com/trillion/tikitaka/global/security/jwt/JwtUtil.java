package com.trillion.tikitaka.global.security.jwt;

import static com.trillion.tikitaka.global.security.constant.AuthenticationConstants.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.trillion.tikitaka.global.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final JwtConfig jwtConfig;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtTokenRepository jwtTokenRepository;

	public ResponseCookie createRefreshTokenCookie(String refreshToken) {
		return ResponseCookie.from(TOKEN_TYPE_REFRESH, refreshToken)
			.httpOnly(true)
			.secure(true)
			.sameSite("None")
			.maxAge(jwtConfig.getRefreshTokenExpirationInMS())
			.path("/")
			.build();
	}

	public boolean isExpired(String token) {
		try {
			Claims claims = jwtTokenProvider.parseClaims(token);
			return claims.getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		}
	}

	public LocalDateTime getIssuedAt(String token) {
		Claims claims = jwtTokenProvider.parseClaims(token);
		Date issuedAt = claims.getIssuedAt();
		if (issuedAt == null) {
			return null;
		}

		return Instant.ofEpochMilli(issuedAt.getTime())
			.atZone(ZoneId.systemDefault())
			.toLocalDateTime();
	}

	public void saveRefreshToken(String username, String refreshToken, Long expiredMs) {
		Date date = new Date(System.currentTimeMillis() + expiredMs);

		JwtToken jwtToken = JwtToken.builder()
			.username(username)
			.refreshToken(refreshToken)
			.expiration(date)
			.build();

		jwtTokenRepository.save(jwtToken);
	}

	public long getExpiration(String token) {
		Claims claims = jwtTokenProvider.parseClaims(token);
		Date expiration = claims.getExpiration();
		if (expiration == null) {
			return 0;
		}

		return expiration.getTime();
	}
}
