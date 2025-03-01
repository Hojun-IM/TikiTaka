package com.trillion.tikitaka.domain.member.infrastructure;

import static com.trillion.tikitaka.domain.member.domain.QMember.*;
import static com.trillion.tikitaka.domain.tickettype.domain.QTicketType.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trillion.tikitaka.domain.member.domain.Role;
import com.trillion.tikitaka.domain.member.dto.MemberInfoListResponse;
import com.trillion.tikitaka.domain.member.dto.MemberInfoResponse;
import com.trillion.tikitaka.domain.member.dto.QMemberInfoResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public MemberInfoResponse getMemberInfo(Long memberId) {
		return queryFactory
			.select(new QMemberInfoResponse(
				member.id,
				member.username,
				member.email,
				member.role,
				member.profileImageUrl
			))
			.from(member)
			.where(
				memberIdCond(memberId),
				deletedAtIsNull()
			)
			.fetchOne();
	}

	@Override
	public MemberInfoListResponse getAllMembersForAdminByRole(Pageable pageable, Role role) {

		List<MemberInfoResponse> content = queryFactory
			.select(new QMemberInfoResponse(
				member.id,
				member.username,
				member.email,
				member.role,
				member.profileImageUrl
			))
			.from(member)
			.where(
				roleCond(role),
				deletedAtIsNull()
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long countQuery = Optional.ofNullable(queryFactory
			.select(member.count())
			.from(member)
			.where(
				roleCond(role),
				deletedAtIsNull()
			)
			.fetchOne()
		).orElse(0L);

		Page<MemberInfoResponse> pageResult = PageableExecutionUtils.getPage(content, pageable, () -> countQuery);

		// 하나의 쿼리로 그룹화 하여 역할별 건수 조회
		List<Tuple> tupleCounts = queryFactory
			.select(member.role, member.count())
			.from(member)
			.where(
				deletedAtIsNull()
			)
			.groupBy(member.role)
			.fetch();

		Map<Role, Long> roleCountMap = tupleCounts.stream()
			.collect(Collectors.toMap(
				tuple -> tuple.get(member.role),
				tuple -> Optional.ofNullable(tuple.get(member.count())).orElse(0L)
			));

		Long adminCount = roleCountMap.getOrDefault(Role.ADMIN, 0L);
		Long managerCount = roleCountMap.getOrDefault(Role.MANAGER, 0L);
		Long userCount = roleCountMap.getOrDefault(Role.USER, 0L);

		// 전체 멤버 객체로 반환
		MemberInfoListResponse response = new MemberInfoListResponse();
		response.setMemberInfo(pageResult);
		response.setRoleCount(adminCount, managerCount, userCount);

		return response;
	}

	@Override
	public List<MemberInfoResponse> getAllMembersForManagerAndUser(Role role) {
		return queryFactory
			.select(new QMemberInfoResponse(
				member.id,
				member.username,
				member.email,
				member.role,
				member.profileImageUrl
			))
			.from(member)
			.where(roleCond(role).and(nonAdminCondition()))
			.fetch();
	}

	private BooleanExpression deletedAtIsNull() {
		return ticketType.deletedAt.isNull();
	}

	private BooleanExpression memberIdCond(Long memberId) {
		return memberId != null ? member.id.eq(memberId) : Expressions.TRUE;
	}

	private BooleanExpression roleCond(Role role) {
		return role != null ? member.role.eq(role) : Expressions.TRUE;
	}

	private BooleanExpression nonAdminCondition() {
		return member.role.ne(Role.ADMIN);
	}
}
