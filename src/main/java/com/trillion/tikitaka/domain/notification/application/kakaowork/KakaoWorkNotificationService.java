package com.trillion.tikitaka.domain.notification.application.kakaowork;

import java.util.List;

import org.springframework.stereotype.Service;

import com.trillion.tikitaka.domain.notification.dto.NotificationMessage;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.KakaoWorkMessageRequest;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.Block;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoWorkNotificationService {

	private final KakaoWorkClient kakaoWorkClient;

	public Mono<Void> sendKakaoWorkNotification(
		NotificationMessage notificationMessage,
		List<Block> blocks
	) {
		log.info("[카카오워크 알림 전송 시작] 이메일: {}, 알림 유형: {}, 메시지: {}",
			notificationMessage.getReceiverEmail(),
			notificationMessage.getType(),
			notificationMessage.getContent()
		);

		return kakaoWorkClient.findUserIdByEmail(notificationMessage.getReceiverEmail())
			.flatMap(userRequest -> {
				String kakaoWorkUserId = userRequest.getUser().getId();
				log.info("[카카오워크 사용자 조회 성공] 사용자 ID: {}", kakaoWorkUserId);

				return kakaoWorkClient.openConversation(kakaoWorkUserId);
			})
			.flatMap(conversationRequest -> {
				String conversationId = conversationRequest.getConversation().getId();
				log.info("[카카오워크 채팅방 열기 성공] 채팅 ID: {}", conversationId);

				KakaoWorkMessageRequest messageRequest = new KakaoWorkMessageRequest(
					conversationId,
					notificationMessage.getContent(),
					blocks
				);
				log.info("[카카오워크 메시지 생성 성공] 메시지: {}", messageRequest);

				return kakaoWorkClient.sendMessage(messageRequest);
			});
	}
}
