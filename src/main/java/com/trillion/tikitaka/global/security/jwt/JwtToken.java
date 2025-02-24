package com.trillion.tikitaka.global.security.jwt;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "jwt_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;
	private String refreshToken;
	private Date expiration;

	@Builder
	public JwtToken(String username, String refreshToken, Date expiration) {
		this.username = username;
		this.refreshToken = refreshToken;
		this.expiration = expiration;
	}
}
