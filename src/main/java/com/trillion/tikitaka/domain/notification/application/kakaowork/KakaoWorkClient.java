package com.trillion.tikitaka.domain.notification.application.kakaowork;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.trillion.tikitaka.domain.notification.dto.kakaowork.KakaoWorkConversationRequest;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.KakaoWorkMessageRequest;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.KakaoWorkUserRequest;
import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;

import reactor.core.publisher.Mono;

@Service
public class KakaoWorkClient {

	private final WebClient kakaoWorkWebClient;

	public KakaoWorkClient(@Qualifier("kakaoWorkWebClient") WebClient kakaoWorkWebClient) {
		this.kakaoWorkWebClient = kakaoWorkWebClient;
	}

	public Mono<KakaoWorkUserRequest> findUserIdByEmail(String email) {
		return kakaoWorkWebClient.get()
			.uri(uriBuilder -> uriBuilder.path("/v1/users.find_by_email")
				.queryParam("email", email)
				.build())
			.retrieve()
			.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
				.flatMap(error -> Mono.error(new BusinessException(ErrorCode.KAKAO_WORK_FETCH_USER_ID_FAILED))))
			.bodyToMono(KakaoWorkUserRequest.class);
	}

	public Mono<KakaoWorkConversationRequest> openConversation(String userId) {
		return kakaoWorkWebClient.post()
			.uri("/v1/conversations.open")
			.bodyValue("{\"user_id\": \"" + userId + "\"}")
			.retrieve()
			.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
				.flatMap(error -> Mono.error(new BusinessException(ErrorCode.KAKAO_WORK_OPEN_CONVERSATION_FAILED))))
			.bodyToMono(KakaoWorkConversationRequest.class);
	}

	public Mono<Void> sendMessage(KakaoWorkMessageRequest messageRequest) {
		return kakaoWorkWebClient.post()
			.uri("/v1/messages.send")
			.bodyValue(messageRequest)
			.retrieve()
			.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
				.flatMap(error -> Mono.error(new BusinessException(ErrorCode.KAKAO_WORK_SEND_MESSAGE_FAILED))))
			.bodyToMono(Void.class);
	}
}
