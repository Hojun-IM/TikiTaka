package com.trillion.tikitaka.domain.notification.application;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trillion.tikitaka.domain.notification.application.email.EmailNotificationService;
import com.trillion.tikitaka.domain.notification.application.kakaowork.KakaoWorkNotificationService;
import com.trillion.tikitaka.domain.notification.domain.Notification;
import com.trillion.tikitaka.domain.notification.domain.NotificationChannel;
import com.trillion.tikitaka.domain.notification.domain.NotificationDomainService;
import com.trillion.tikitaka.domain.notification.domain.NotificationStatus;
import com.trillion.tikitaka.domain.notification.dto.NotificationMessage;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.Block;
import com.trillion.tikitaka.domain.notification.infrastructure.NotificationPreferenceRepository;
import com.trillion.tikitaka.domain.notification.infrastructure.NotificationRepository;
import com.trillion.tikitaka.domain.notification.util.NotificationMessageBuilder;
import com.trillion.tikitaka.domain.notification.util.NotificationMessageBuilderFactory;
import com.trillion.tikitaka.global.config.RabbitMqConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

	private final ObjectMapper objectMapper;
	private final NotificationMessageBuilderFactory builderFactory;
	private final NotificationDomainService notificationDomainService;
	private final NotificationRepository notificationRepository;
	private final NotificationPreferenceRepository preferenceRepository;
	private final KakaoWorkNotificationService kakaoWorkNotificationService;
	private final EmailNotificationService emailNotificationService;

	@RabbitListener(queues = RabbitMqConfig.KAKAOWORK_QUEUE)
	public void consumeKakaoWork(String messageJson) {
		handleNotificationMessage(messageJson, NotificationChannel.KAKAOWORK);
	}

	@RabbitListener(queues = RabbitMqConfig.EMAIL_QUEUE)
	public void consumeEmail(String messageJson) {
		handleNotificationMessage(messageJson, NotificationChannel.EMAIL);
	}

	private void handleNotificationMessage(String rawMessageJson, NotificationChannel channel) {
		log.info("[알림 서비스] 알림 큐({}) 수신: {}", channel, rawMessageJson);

		try {
			NotificationMessage message = objectMapper.readValue(rawMessageJson, NotificationMessage.class);

			// 활성화 된 채널 확인
			if (!checkChannelEnabled(message.getReceiverId(), channel)) {
				log.info("[알림 서비스] 알림 채널 OFF, 사용자 ID: {}, 채널: {}", message.getReceiverId(), channel);
				Notification notification = notificationDomainService.saveNotificationLog(
					message, channel, NotificationStatus.SKIPPED, "알림 채널 OFF", 0
				);
				notificationRepository.save(notification);
				return;
			}

			// 알림 메시지 빌드 및 채널별 전송
			NotificationMessageBuilder builder = builderFactory.getBuilder(message.getType());

			switch (channel) {
				case KAKAOWORK -> sendKakaoWorkChannel(message, builder);
				case EMAIL -> sendToEmailChannel(message, builder);
			}
		} catch (Exception e) {
			log.error("[알림 서비스] 알림 큐({}) 처리 중 예외: {}", channel, e.getMessage(), e);
			// TODO: DLQ 전송 및 재시도 로직
		}
	}

	private void sendKakaoWorkChannel(NotificationMessage msg, NotificationMessageBuilder builder) {
		try {
			List<Block> blocks = builder.buildKakaoWorkMessage(msg);

			Mono<Void> sendMono = kakaoWorkNotificationService.sendKakaoWorkNotification(msg, blocks);

			sendMono.subscribe(
				unused -> {
				},
				error -> {
					log.error("[카카오워크 알림] 전송 실패, 수신자: {}", msg.getReceiverId());
					Notification fail = notificationDomainService.saveNotificationLog(
						msg, NotificationChannel.KAKAOWORK, NotificationStatus.FAIL, error.getMessage(), 0
					);
					notificationRepository.save(fail);
				},
				() -> {
					log.info("[카카오워크 알림] 전송 성공, 수신자: {}", msg.getReceiverId());
					Notification success = notificationDomainService.saveNotificationLog(
						msg, NotificationChannel.KAKAOWORK, NotificationStatus.SUCCESS, null, 0
					);
					notificationRepository.save(success);
				}
			);
		} catch (Exception e) {
			log.error("[카카오워크 전송 준비 과정 실패] {}", e.getMessage(), e);
			notificationDomainService.saveNotificationLog(
				msg, NotificationChannel.KAKAOWORK, NotificationStatus.FAIL, e.getMessage(), 0
			);
		}
	}

	private void sendToEmailChannel(NotificationMessage msg, NotificationMessageBuilder builder) {
		try {
			String htmlContent = builder.buildEmailMessage(msg);
			if (htmlContent == null) {
				htmlContent = msg.getContent();
			}

			boolean success = emailNotificationService.sendEmail(msg, htmlContent);

			if (success) {
				notificationDomainService.saveNotificationLog(
					msg, NotificationChannel.EMAIL, NotificationStatus.SUCCESS, null, 0
				);
			} else {
				notificationDomainService.saveNotificationLog(
					msg, NotificationChannel.EMAIL, NotificationStatus.FAIL,
					"이메일 전송 실패", 0
				);
			}
		} catch (Exception e) {
			log.error("[이메일 전송 준비 과정 실패] {}", e.getMessage(), e);
			notificationDomainService.saveNotificationLog(
				msg, NotificationChannel.EMAIL, NotificationStatus.FAIL,
				"이메일 메시지 빌드 실패: " + e.getMessage(), 0
			);
		}
	}

	private boolean checkChannelEnabled(Long memberId, NotificationChannel channel) {
		if (memberId == null) {
			return true;
		}

		return preferenceRepository.existsByMemberIdAndChannel(memberId, channel);
	}
}
