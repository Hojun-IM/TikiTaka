package com.trillion.tikitaka.domain.member.domain;

import static com.trillion.tikitaka.global.security.constant.AuthenticationConstants.*;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class MemberDomainService {

	public Member createMember(String username, String email, Role role, String encodedPassword) {
		return Member.builder()
			.username(username)
			.email(email)
			.password(encodedPassword)
			.role(role)
			.build();
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

	public void updatePassword(Member member, String encodedPassword) {
		member.updatePassword(encodedPassword);
	}

	public boolean isSamePassword(String password, String newPassword) {
		return password.equals(newPassword);
	}

	public void updateRole(Member member, Role role) {
		member.updateRole(role);
	}
}
