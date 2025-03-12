package com.trillion.tikitaka.domain.notification.dto.kakaowork;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoWorkConversationRequest {
	private Conversation conversation;
	private boolean isNew;
	private boolean success;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Conversation {
		private String id;
		private String name;
	}
}
