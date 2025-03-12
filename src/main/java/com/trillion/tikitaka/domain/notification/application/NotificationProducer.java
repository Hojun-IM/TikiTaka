package com.trillion.tikitaka.domain.notification.application;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trillion.tikitaka.domain.notification.domain.NotificationChannel;
import com.trillion.tikitaka.domain.notification.dto.NotificationMessage;
import com.trillion.tikitaka.domain.notification.infrastructure.NotificationPreferenceRepository;
import com.trillion.tikitaka.global.config.RabbitMqConfig;
import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

	private final ObjectMapper objectMapper;
	private final RabbitTemplate rabbitTemplate;
	private final NotificationPreferenceRepository preferenceRepository;

	public void sendNotificationToEnabledChannels(NotificationMessage message) {
		Long receiverId = message.getReceiverId();
		if (receiverId == null) {
			log.warn("[알림 발행 경고] receiverId가 null입니다. 메시지: {}", message);
			return;
		}

		List<NotificationChannel> enabledChannels = preferenceRepository.findEnabledChannelsByMemberId(receiverId);

		if (enabledChannels.isEmpty()) {
			log.info("[알림 발행] 사용자 {}가 활성화한 채널이 없어 전송 스킵. 메시지: {}", receiverId, message);
			return;
		}

		this.sendNotification(message, enabledChannels);
	}

	public void sendNotification(NotificationMessage message, List<NotificationChannel> channels) {
		for (NotificationChannel channel : channels) {
			String routingKey = getRoutingKey(channel);

			try {
				String json = objectMapper.writeValueAsString(message);
				rabbitTemplate.convertAndSend(
					RabbitMqConfig.EXCHANGE_NAME,
					routingKey,
					json
				);
				log.info("[알림 발행 성공] 채널: {}, 라우팅 키: {}, 메시지: {}", channel, routingKey, json);
			} catch (JsonProcessingException e) {
				log.error("[알림 발행 실패] 알림 메시지를 JSON으로 변환하는데 실패: {}", e.getMessage());
				throw new BusinessException(ErrorCode.NOTIFICATION_JSON_PARSE_FAILED);
			}
		}
	}

	private String getRoutingKey(NotificationChannel channel) {
		return switch (channel) {
			case KAKAOWORK -> "notification.kakaowork";
			case EMAIL -> "notification.email";
		};
	}
}
