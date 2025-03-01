package com.trillion.tikitaka.domain.ticket.infrastructure;

import static com.trillion.tikitaka.domain.ticket.domain.QTicket.*;
import static com.trillion.tikitaka.domain.tickettype.domain.QTicketType.*;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trillion.tikitaka.domain.ticket.dto.QTicketResponse;
import com.trillion.tikitaka.domain.ticket.dto.TicketResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomTicketRepositoryImpl implements CustomTicketRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public TicketResponse getTicket(Long ticketId) {

		return queryFactory
			.select(new QTicketResponse(
				ticket.id.as("ticketId"),
				ticket.title,
				ticket.content,
				ticket.priority,
				ticket.status,
				ticket.ticketType.id.as("typeId"),
				ticket.ticketType.name.as("typeName"),
				ticket.primaryCategory.id.as("primaryCategoryId"),
				ticket.primaryCategory.name.as("primaryCategoryName"),
				ticket.secondaryCategory.id.as("secondaryCategoryId"),
				ticket.secondaryCategory.name.as("secondaryCategoryName"),
				ticket.manager.id.as("managerId"),
				ticket.manager.username.as("managerName"),
				ticket.requester.id.as("requesterId"),
				ticket.requester.username.as("requesterName"),
				ticket.urgent,
				ticket.deadline,
				ticket.createdAt,
				ticket.updatedAt
			))
			.from(ticket)
			.where(
				ticketIdCond(ticketId),
				deletedAtIsNull()
			)
			.fetchOne();
	}

	private BooleanExpression deletedAtIsNull() {
		return ticketType.deletedAt.isNull();
	}

	private BooleanExpression ticketIdCond(Long ticketId) {
		return ticket.id.eq(ticketId);
	}
}
