package com.trillion.tikitaka.global.security.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
	Boolean existsByRefreshToken(String refreshToken);

	void deleteByRefreshToken(String refreshToken);
}
