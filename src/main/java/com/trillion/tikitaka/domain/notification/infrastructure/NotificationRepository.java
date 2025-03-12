package com.trillion.tikitaka.domain.notification.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trillion.tikitaka.domain.notification.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
