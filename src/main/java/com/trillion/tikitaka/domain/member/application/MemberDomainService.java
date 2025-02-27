package com.trillion.tikitaka.domain.member.application;

import static com.trillion.tikitaka.global.security.constant.AuthenticationConstants.*;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.domain.member.domain.Role;
import com.trillion.tikitaka.domain.member.infrastructure.MemberRepository;
import com.trillion.tikitaka.domain.registration.application.PasswordGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberDomainService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	public Member createMember(String username, String email, Role role) {
		String password = PasswordGenerator.generateRandomPassword();
		String encodedPassword = passwordEncoder.encode(password);

		Member member = Member.builder()
			.username(username)
			.email(email)
			.password(encodedPassword)
			.role(role)
			.build();
		memberRepository.save(member);

		return member;
	}

	public void processLoginFailure(Member member) {
		member.increaseLoginFailureCount();

		if (member.getLoginFailureCount() >= MAX_LOGIN_FAILURE_COUNT) {
			member.lockAccount(LocalDateTime.now().plusMinutes(30));
		}
	}

	public void processLoginSuccess(Member member) {
		member.unlockAccount();
		member.updateLastLoginAt(LocalDateTime.now());
	}

	public void resetLoginFailCount(Member member) {
		member.resetLoginFailCount();
	}

	public void updatePassword(Member member, String newPassword) {
		String encodedPassword = passwordEncoder.encode(newPassword);
		member.updatePassword(encodedPassword);
	}

	public boolean isSamePassword(String newPassword, String password) {
		return newPassword.equals(password);
	}

	public boolean isValidPassword(String password, Member member) {
		return passwordEncoder.matches(password, member.getPassword());
	}

	public void updateRole(Member member, Role role) {
		member.updateRole(role);
	}
}
