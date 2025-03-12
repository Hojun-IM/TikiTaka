package com.trillion.tikitaka.domain.notification.dto.kakaowork;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoWorkUserRequest {
	private boolean success;
	private com.trillion.tikitaka.domain.notification.dto.kakaowork.KakaoWorkUserRequest.User user;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class User {
		private String id;
		private String name;
	}
}
