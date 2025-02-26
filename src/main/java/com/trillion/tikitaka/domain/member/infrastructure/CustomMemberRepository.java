package com.trillion.tikitaka.domain.member.infrastructure;

import com.trillion.tikitaka.domain.member.dto.MemberInfoResponse;

public interface CustomMemberRepository {

	MemberInfoResponse getMyInfo(Long memberId);
}
