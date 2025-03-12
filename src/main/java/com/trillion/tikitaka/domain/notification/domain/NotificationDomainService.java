package com.trillion.tikitaka.domain.notification.domain;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.trillion.tikitaka.domain.notification.dto.NotificationMessage;

@Service
public class NotificationDomainService {

	public NotificationMessage buildNotificationMessage(
		NotificationType type,
		Long senderId,
		String senderEmail,
		Long receiverId,
		String receiverEmail,
		String title,
		String content,
		Map<String, Object> details
	) {
		return NotificationMessage.builder()
			.type(type)
			.senderId(senderId)
			.senderEmail(senderEmail)
			.receiverId(receiverId)
			.receiverEmail(receiverEmail)
			.title(title)
			.content(content)
			.details(details)
			.build();
	}

	public Notification saveNotificationLog(
		NotificationMessage message,
		NotificationChannel channel,
		NotificationStatus status,
		String failReason,
		int retryCount
	) {
		return Notification.builder()
			.type(message.getType())
			.channel(channel)
			.status(status)
			.senderId(message.getSenderId())
			.receiverId(message.getReceiverId())
			.title(message.getTitle())
			.content(message.getContent())
			.failReason(failReason == null ? "" : failReason)
			.retryCount(retryCount)
			.build();
	}

	public NotificationPreference initializeNotificationPreference(Long memberId) {
		return NotificationPreference.builder()
			.memberId(memberId)
			.channel(NotificationChannel.KAKAOWORK)
			.enabled(true)
			.build();
	}
}
