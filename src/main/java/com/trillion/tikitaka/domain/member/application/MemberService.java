package com.trillion.tikitaka.domain.member.application;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trillion.tikitaka.domain.member.domain.Role;
import com.trillion.tikitaka.domain.member.dto.MemberInfoListResponse;
import com.trillion.tikitaka.domain.member.dto.MemberInfoResponse;
import com.trillion.tikitaka.domain.member.infrastructure.MemberRepository;
import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;
import com.trillion.tikitaka.global.security.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

	private final MemberDomainService memberDomainService;
	private final MemberRepository memberRepository;

	// 내 정보 조회
	public MemberInfoResponse getMyInfo(CustomUserDetails userDetails) {
		log.info("[내 정보 조회] 사용자 아이디: {}", userDetails.getMember().getUsername());
		return memberRepository.getMyInfo(userDetails.getMember().getId());
	}

	// 관리자용 전체 멤버 조회
	public MemberInfoListResponse getAllMembersForAdmin(Pageable pageable, Role role) {
		log.info("[관리자용 전체 멤버 조회] 페이지: {}, 사이즈: {}, 역할: {}", pageable.getPageNumber(), pageable.getPageSize(), role);
		return memberRepository.getAllMembersForAdminByRole(pageable, role);
	}

	// 매니저, 사용자용 전체 멤버 조회
	public List<MemberInfoResponse> getAllMembersForManagerAndUser(Role role) {
		log.info("[매니저, 사용자용 전체 멤버 조회] 역할: {}", role);
		if (role == Role.ADMIN) {
			log.error("[매니저, 사용자용 전체 멤버 조회] 관리자를 조회할 수 없습니다.");
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}

		return memberRepository.getAllMembersForManagerAndUser(role);
	}
}
