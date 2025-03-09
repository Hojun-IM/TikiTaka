package com.trillion.tikitaka.domain.member.infrastructure;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.domain.member.domain.Role;
import com.trillion.tikitaka.domain.member.dto.MemberInfoListResponse;
import com.trillion.tikitaka.domain.member.dto.MemberInfoResponse;

public interface CustomMemberRepository {

	MemberInfoResponse getMemberInfo(Long memberId);

	MemberInfoListResponse getAllMembersForAdminByRole(Pageable pageable, Role role);

	List<MemberInfoResponse> getAllMembersForManagerAndUser(Role role);

	List<Member> findAllManagers();
}
