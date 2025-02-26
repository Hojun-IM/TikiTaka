package com.trillion.tikitaka.domain.member.infrastructure;

import org.springframework.data.domain.Pageable;

import com.trillion.tikitaka.domain.member.domain.Role;
import com.trillion.tikitaka.domain.member.dto.MemberInfoListResponse;
import com.trillion.tikitaka.domain.member.dto.MemberInfoResponse;

public interface CustomMemberRepository {

	MemberInfoResponse getMyInfo(Long memberId);

	MemberInfoListResponse getAllMembersForAdminByRole(Pageable pageable, Role role);
}
