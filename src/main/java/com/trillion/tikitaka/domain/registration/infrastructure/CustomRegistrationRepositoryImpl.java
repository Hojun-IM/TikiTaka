package com.trillion.tikitaka.domain.registration.infrastructure;

import static com.trillion.tikitaka.domain.registration.domain.QRegistration.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trillion.tikitaka.domain.registration.domain.RegistrationStatus;
import com.trillion.tikitaka.domain.registration.dto.QRegistrationListResponse;
import com.trillion.tikitaka.domain.registration.dto.RegistrationListResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomRegistrationRepositoryImpl implements CustomRegistrationRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<RegistrationListResponse> getRegistrations(RegistrationStatus status, Pageable pageable) {

		List<RegistrationListResponse> content = queryFactory
			.select(new QRegistrationListResponse(
				registration.id.as("registrationId"),
				registration.username,
				registration.email,
				registration.status,
				registration.createdAt,
				registration.updatedAt
			))
			.from(registration)
			.where(
				statusCond(status),
				deletedAtIsNull()
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(registration.count())
			.from(registration)
			.where(
				statusCond(status),
				deletedAtIsNull()
			);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	private BooleanExpression deletedAtIsNull() {
		return registration.deletedAt.isNull();
	}

	private BooleanExpression statusCond(RegistrationStatus status) {
		return status != null ? registration.status.eq(status) : null;
	}
}
