package com.trillion.tikitaka.domain.member.infrastructure;

import static com.trillion.tikitaka.domain.member.domain.QMember.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trillion.tikitaka.domain.member.dto.MemberInfoResponse;
import com.trillion.tikitaka.domain.member.dto.QMemberInfoResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public MemberInfoResponse getMyInfo(Long memberId) {
		return queryFactory
			.select(new QMemberInfoResponse(
				member.id,
				member.username,
				member.email,
				member.role,
				member.profileImageUrl
			))
			.from(member)
			.where(member.id.eq(memberId))
			.fetchOne();
	}
}
