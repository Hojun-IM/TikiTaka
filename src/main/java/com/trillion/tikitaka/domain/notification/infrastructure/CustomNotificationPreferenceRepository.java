package com.trillion.tikitaka.domain.notification.infrastructure;

import java.util.List;

import com.trillion.tikitaka.domain.notification.domain.NotificationChannel;

public interface CustomNotificationPreferenceRepository {
	List<NotificationChannel> findEnabledChannelsByMemberId(Long memberId);
}
