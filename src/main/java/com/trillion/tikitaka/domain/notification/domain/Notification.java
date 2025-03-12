package com.trillion.tikitaka.domain.notification.domain;

import com.trillion.tikitaka.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationChannel channel;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationStatus status;

	private Long senderId;

	private Long receiverId;

	@Column(nullable = false)
	private String title;

	@Lob
	@Column(nullable = false, columnDefinition = "LONGTEXT")
	private String content;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String failReason;

	private int retryCount;

	@Builder
	public Notification(
		NotificationType type, NotificationChannel channel, NotificationStatus status, Long senderId,
		Long receiverId, String title, String content, String failReason, int retryCount
	) {
		this.type = type;
		this.channel = channel;
		this.status = status;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.title = title;
		this.content = content;
		this.failReason = failReason;
		this.retryCount = retryCount;
	}
}
