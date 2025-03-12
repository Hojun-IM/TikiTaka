package com.trillion.tikitaka.domain.notification.infrastructure;

import static com.trillion.tikitaka.domain.notification.domain.QNotificationPreference.*;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trillion.tikitaka.domain.notification.domain.NotificationChannel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomNotificationPreferenceRepositoryImpl implements CustomNotificationPreferenceRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<NotificationChannel> findEnabledChannelsByMemberId(Long memberId) {
		return queryFactory
			.select(notificationPreference.channel)
			.from(notificationPreference)
			.where(
				memberIdCond(memberId).and(channelEnabledCond())
			)
			.fetch();
	}

	private BooleanExpression memberIdCond(Long memberId) {
		return memberId != null ? notificationPreference.memberId.eq(memberId) : null;
	}

	private BooleanExpression channelEnabledCond() {
		return notificationPreference.enabled.isTrue();
	}
}
