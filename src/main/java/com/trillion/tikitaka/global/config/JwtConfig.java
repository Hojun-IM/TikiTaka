package com.trillion.tikitaka.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class JwtConfig {

	@Value("${spring.jwt.secret-key}")
	private String secretKey;

	@Value("${spring.jwt.access-token-expiration-in-ms}")
	private long accessTokenExpirationInMS;

	@Value("${spring.jwt.refresh-token-expiration-in-ms}")
	private long refreshTokenExpirationInMS;
}
