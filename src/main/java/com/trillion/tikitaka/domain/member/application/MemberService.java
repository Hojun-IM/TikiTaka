package com.trillion.tikitaka.domain.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trillion.tikitaka.domain.member.dto.MemberInfoResponse;
import com.trillion.tikitaka.domain.member.infrastructure.MemberRepository;
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
}
