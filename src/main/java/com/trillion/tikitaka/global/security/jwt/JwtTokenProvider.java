package com.trillion.tikitaka.global.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.trillion.tikitaka.global.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	private final JwtConfig jwtConfig;
	private final SecretKey secretKey;

	public JwtTokenProvider(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
		this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
	}

	public String createToken(Long memberId, String username, String role, Long expiredTimeMs, String type) {
		return Jwts.builder()
			.claim("id", memberId)
			.claim("username", username)
			.claim("role", role)
			.claim("type", type)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + expiredTimeMs))
			.signWith(secretKey)
			.compact();
	}

	public Claims parseClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	public Long getMemberId(String token) {
		return parseClaims(token).get("id", Long.class);
	}

	public String getUsername(String token) {
		return parseClaims(token).get("username", String.class);
	}

	public String getRole(String token) {
		return parseClaims(token).get("role", String.class);
	}

	public String getType(String token) {
		return parseClaims(token).get("type", String.class);
	}
}
