package com.trillion.tikitaka.domain.notification.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trillion.tikitaka.domain.notification.domain.NotificationChannel;
import com.trillion.tikitaka.domain.notification.domain.NotificationPreference;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long>,
	CustomNotificationPreferenceRepository {
	boolean existsByMemberIdAndChannel(Long memberId, NotificationChannel channel);
}
