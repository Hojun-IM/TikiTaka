package com.trillion.tikitaka.global.security.application;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.domain.member.infrastructure.MemberRepository;
import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;
import com.trillion.tikitaka.global.security.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		Member member = memberRepository.findByUsername(username).orElseThrow(() -> {
			log.error("[인증 오류] {} 회원을 찾을 수 없습니다.", username);
			return new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
		});

		return new CustomUserDetails(member);
	}
}
