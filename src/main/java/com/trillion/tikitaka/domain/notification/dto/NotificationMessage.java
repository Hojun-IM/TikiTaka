package com.trillion.tikitaka.domain.notification.dto;

import java.util.Map;

import com.trillion.tikitaka.domain.notification.domain.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
	private NotificationType type;
	private Long senderId;
	private String senderEmail;
	private Long receiverId;
	private String receiverEmail;
	private String title;
	private String content;
	private Map<String, Object> details;
}
