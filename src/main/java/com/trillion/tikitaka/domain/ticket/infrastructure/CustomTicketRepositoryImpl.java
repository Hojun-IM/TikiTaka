package com.trillion.tikitaka.domain.ticket.infrastructure;

import static com.trillion.tikitaka.domain.ticket.domain.QTicket.*;
import static com.trillion.tikitaka.domain.tickettype.domain.QTicketType.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trillion.tikitaka.domain.category.domain.QCategory;
import com.trillion.tikitaka.domain.member.domain.QMember;
import com.trillion.tikitaka.domain.ticket.domain.TicketPriority;
import com.trillion.tikitaka.domain.ticket.domain.TicketStatus;
import com.trillion.tikitaka.domain.ticket.dto.QTicketListResponseForManager;
import com.trillion.tikitaka.domain.ticket.dto.QTicketListResponseForUser;
import com.trillion.tikitaka.domain.ticket.dto.QTicketResponse;
import com.trillion.tikitaka.domain.ticket.dto.TicketFilter;
import com.trillion.tikitaka.domain.ticket.dto.TicketListResponseForManager;
import com.trillion.tikitaka.domain.ticket.dto.TicketListResponseForUser;
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

	@Override
	public Page<TicketListResponseForManager> getTicketsForManager(TicketFilter filter) {

		QMember manager = new QMember("manager");
		QCategory primaryCategory = new QCategory("primaryCategory");
		QCategory secondaryCategory = new QCategory("secondaryCategory");

		List<TicketListResponseForManager> content = queryFactory
			.select(new QTicketListResponseForManager(
				ticket.id.as("ticketId"),
				ticket.title,
				ticket.content,
				ticket.priority,
				ticket.status,
				ticketType.name.as("typeName"),
				primaryCategory.name.as("primaryCategoryName"),
				secondaryCategory.name.as("secondaryCategoryName"),
				manager.id.as("managerId"),
				manager.username.as("managerName"),
				ticket.urgent,
				ticket.deadline,
				ticket.createdAt
			))
			.from(ticket)
			.leftJoin(ticket.ticketType, ticketType)
			.leftJoin(ticket.primaryCategory, primaryCategory)
			.leftJoin(ticket.secondaryCategory, secondaryCategory)
			.leftJoin(ticket.manager, manager)
			.where(
				deletedAtIsNull(),
				statusCond(filter.getStatus()),
				priorityCond(filter.getPriority()),
				managerIdCond(filter.getManagerId()),
				typeIdCond(filter.getTypeId()),
				primaryCategoryIdCond(filter.getPrimaryCategoryId()),
				secondaryCategoryIdCond(filter.getPrimaryCategoryId(), filter.getSecondaryCategoryId()),
				urgentCond(filter.getUrgent()),
				keywordCond(filter.getKeyword())
			)
			.orderBy(
				urgentOrderCond(),
				sortCond(filter.getSort())
			)
			.offset(filter.getPageable().getOffset())
			.limit(filter.getPageable().getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(ticket.count())
			.from(ticket)
			.leftJoin(ticket.ticketType, ticketType)
			.leftJoin(ticket.primaryCategory, primaryCategory)
			.leftJoin(ticket.secondaryCategory, secondaryCategory)
			.leftJoin(ticket.manager, manager)
			.where(
				deletedAtIsNull(),
				statusCond(filter.getStatus()),
				priorityCond(filter.getPriority()),
				managerIdCond(filter.getManagerId()),
				typeIdCond(filter.getTypeId()),
				primaryCategoryIdCond(filter.getPrimaryCategoryId()),
				secondaryCategoryIdCond(filter.getPrimaryCategoryId(), filter.getSecondaryCategoryId()),
				urgentCond(filter.getUrgent()),
				keywordCond(filter.getKeyword())
			);

		return PageableExecutionUtils.getPage(content, filter.getPageable(), countQuery::fetchOne);
	}

	@Override
	public Page<TicketListResponseForUser> getTicketsForUser(TicketFilter filter, Long requesterId) {

		QMember manager = new QMember("manager");
		QCategory primaryCategory = new QCategory("primaryCategory");
		QCategory secondaryCategory = new QCategory("secondaryCategory");

		List<TicketListResponseForUser> content = queryFactory
			.select(new QTicketListResponseForUser(
				ticket.id.as("ticketId"),
				ticket.title,
				ticket.content,
				ticket.status,
				ticketType.name.as("typeName"),
				primaryCategory.name.as("primaryCategoryName"),
				secondaryCategory.name.as("secondaryCategoryName"),
				manager.id.as("managerId"),
				manager.username.as("managerName"),
				ticket.urgent,
				ticket.deadline,
				ticket.createdAt
			))
			.from(ticket)
			.leftJoin(ticket.ticketType, ticketType)
			.leftJoin(ticket.primaryCategory, primaryCategory)
			.leftJoin(ticket.secondaryCategory, secondaryCategory)
			.leftJoin(ticket.manager, manager)
			.where(
				deletedAtIsNull(),
				statusCond(filter.getStatus()),
				priorityCond(filter.getPriority()),
				managerIdCond(filter.getManagerId()),
				requesterIdCond(requesterId),
				typeIdCond(filter.getTypeId()),
				primaryCategoryIdCond(filter.getPrimaryCategoryId()),
				secondaryCategoryIdCond(filter.getPrimaryCategoryId(), filter.getSecondaryCategoryId()),
				urgentCond(filter.getUrgent()),
				keywordCond(filter.getKeyword())
			)
			.orderBy(
				urgentOrderCond(),
				sortCond(filter.getSort())
			)
			.offset(filter.getPageable().getOffset())
			.limit(filter.getPageable().getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(ticket.count())
			.from(ticket)
			.leftJoin(ticket.ticketType, ticketType)
			.leftJoin(ticket.primaryCategory, primaryCategory)
			.leftJoin(ticket.secondaryCategory, secondaryCategory)
			.leftJoin(ticket.manager, manager)
			.where(
				deletedAtIsNull(),
				statusCond(filter.getStatus()),
				priorityCond(filter.getPriority()),
				managerIdCond(filter.getManagerId()),
				requesterIdCond(requesterId),
				typeIdCond(filter.getTypeId()),
				primaryCategoryIdCond(filter.getPrimaryCategoryId()),
				secondaryCategoryIdCond(filter.getPrimaryCategoryId(), filter.getSecondaryCategoryId()),
				urgentCond(filter.getUrgent()),
				keywordCond(filter.getKeyword())
			);

		return PageableExecutionUtils.getPage(content, filter.getPageable(), countQuery::fetchOne);
	}

	private BooleanExpression deletedAtIsNull() {
		return ticket.deletedAt.isNull();
	}

	private BooleanExpression ticketIdCond(Long ticketId) {
		return ticket.id.eq(ticketId);
	}

	private BooleanExpression statusCond(TicketStatus status) {
		return status != null ? ticket.status.eq(status) : null;
	}

	private BooleanExpression priorityCond(TicketPriority priority) {
		return priority != null ? ticket.priority.eq(priority) : null;
	}

	private BooleanExpression managerIdCond(Long managerId) {
		return managerId != null ? ticket.manager.id.eq(managerId) : null;
	}

	private BooleanExpression requesterIdCond(Long requesterId) {
		return requesterId != null ? ticket.requester.id.eq(requesterId) : null;
	}

	private BooleanExpression typeIdCond(Long typeId) {
		return typeId != null ? ticket.ticketType.id.eq(typeId) : null;
	}

	private BooleanExpression primaryCategoryIdCond(Long primaryCategoryId) {
		return primaryCategoryId != null ? ticket.primaryCategory.id.eq(primaryCategoryId) : null;
	}

	private BooleanExpression secondaryCategoryIdCond(Long primaryCategoryId, Long secondaryCategoryId) {
		if (secondaryCategoryId == null) {
			return null;
		}
		BooleanExpression expr = ticket.secondaryCategory.id.eq(secondaryCategoryId);
		if (primaryCategoryId != null) {
			expr = expr.and(ticket.primaryCategory.id.eq(primaryCategoryId));
		}
		return expr;
	}

	private BooleanExpression urgentCond(Boolean urgent) {
		return urgent != null ? ticket.urgent.eq(urgent) : null;
	}

	private OrderSpecifier<?> sortCond(String sort) {
		if (sort == null || sort.equalsIgnoreCase("latest")) {
			return ticket.createdAt.desc();
		} else if (sort.equalsIgnoreCase("oldest")) {
			return ticket.createdAt.asc();
		} else if (sort.equalsIgnoreCase("deadline")) {
			return ticket.deadline.asc();
		}
		return ticket.createdAt.desc();
	}

	private OrderSpecifier<?> urgentOrderCond() {
		return Expressions.numberPath(Integer.class, "urgentPriority").asc();
		// NumberExpression<Integer> urgentPriorityExpr = new CaseBuilder()
		// 	.when(ticket.urgent.eq(true)
		// 		.and(ticket.status.in(TicketStatus.PENDING, TicketStatus.IN_PROGRESS, TicketStatus.REVIEW)))
		// 	.then(0)
		// 	.otherwise(1);
		// return urgentPriorityExpr.asc();
	}

	private BooleanExpression keywordCond(String keyword) {
		return (keyword != null && !keyword.isEmpty())
			? ticket.title.containsIgnoreCase(keyword)
			: null;
	}
}
