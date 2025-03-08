package com.trillion.tikitaka.domain.notification.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.trillion.tikitaka.domain.notification.domain.NotificationType;
import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;

@Service
public class NotificationMessageBuilderFactory {

	private final Map<NotificationType, NotificationMessageBuilder> builderMap = new HashMap<>();

	/**
	 * NotificationMessageBuilder 인터페이스를 구현한 모든 빈을 주입받음
	 * 각 빈의 @NotificationTypeQualifier 값을 읽어 Map에 등록
	 * 알림 타입에 해당하는 빌더를 찾아서 반환
	 */
	public NotificationMessageBuilderFactory(List<NotificationMessageBuilder> builders) {
		for (NotificationMessageBuilder builder : builders) {
			NotificationTypeQualifier annotation = builder.getClass().getAnnotation(NotificationTypeQualifier.class);

			if (annotation != null) {
				builderMap.put(annotation.value(), builder);
			}
		}
	}

	// 적절한 빌더를 찾아서 반환
	public NotificationMessageBuilder getBuilder(NotificationType type) {
		if (!builderMap.containsKey(type)) {
			throw new BusinessException(ErrorCode.NOTIFICATION_MESSAGE_BUILDER_NOT_FOUND);
		}
		return builderMap.get(type);
	}
}
