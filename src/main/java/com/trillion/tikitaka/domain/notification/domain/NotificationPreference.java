package com.trillion.tikitaka.domain.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "notification_preference")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationPreference {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long memberId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationChannel channel;

	@Column(nullable = false)
	private boolean enabled;

	@Builder
	public NotificationPreference(Long memberId, NotificationChannel channel, boolean enabled) {
		this.memberId = memberId;
		this.channel = channel;
		this.enabled = enabled;
	}
}
