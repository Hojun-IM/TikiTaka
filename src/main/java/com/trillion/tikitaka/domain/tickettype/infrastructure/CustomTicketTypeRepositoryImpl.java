package com.trillion.tikitaka.domain.tickettype.infrastructure;

import static com.trillion.tikitaka.domain.tickettype.domain.QTicketType.*;

import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trillion.tikitaka.domain.tickettype.dto.QTicketTypeListResponse;
import com.trillion.tikitaka.domain.tickettype.dto.TicketTypeListResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomTicketTypeRepositoryImpl implements CustomTicketTypeRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<TicketTypeListResponse> getTicketTypes() {
		return queryFactory
			.select(new QTicketTypeListResponse(
				ticketType.id.as("typeId"),
				ticketType.name.as("typeName")
			))
			.from(ticketType)
			.where(deletedAtIsNull())
			.orderBy(
				defaultTypeOrder(),
				createdAtOrder()
			)
			.fetch();
	}

	private BooleanExpression deletedAtIsNull() {
		return ticketType.deletedAt.isNull();
	}

	private OrderSpecifier<Boolean> defaultTypeOrder() {
		return ticketType.defaultType.desc();
	}

	private OrderSpecifier<LocalDateTime> createdAtOrder() {
		return ticketType.createdAt.asc();
	}
}
