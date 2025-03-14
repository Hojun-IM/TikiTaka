package com.trillion.tikitaka.domain.member;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.domain.member.domain.MemberDomainService;
import com.trillion.tikitaka.domain.member.domain.Role;

/**
 * 테스트 주체: MemberDomainService
 * 협력 객체: Member
 */
@DisplayName("MemberDomainService 테스트")
public class MemberDomainServiceTest {

	private final MemberDomainService memberDomainService = new MemberDomainService();

	@Test
	@DisplayName("createMember로 새 회원 생성 시, Member 객체가 정상 생성되어야 한다.")
	void should_createMember_when_createMember() {
		// given
		String username = "testUser";
		String email = "test@test.com";
		String encodedPassword = "encodedPassword";
		Role role = Role.USER;

		// when
		Member member = memberDomainService.createMember(username, email, role, encodedPassword);

		// then
		assertAll(
			() -> assertThat(member).isNotNull(),
			() -> assertThat(member.getUsername()).isEqualTo(username),
			() -> assertThat(member.getEmail()).isEqualTo(email),
			() -> assertThat(member.getPassword()).isEqualTo(encodedPassword),
			() -> assertThat(member.getRole()).isEqualTo(Role.USER)
		);
	}

	@Test
	@DisplayName("processLoginFailure로 로그인 실패 처리 시, 실패 횟수가 증가한다.")
	void should_increaseLoginFailureCount_when_processLoginFailure() {
		// given
		Member member = Member.builder()
			.username("testUser")
			.password("encodedPassword")
			.email("test@test.com")
			.role(Role.USER)
			.loginFailureCount(0)
			.build();

		// when
		memberDomainService.processLoginFailure(member);

		// then
		assertThat(member.getLoginFailureCount()).isEqualTo(1);
		assertThat(member.isAccountNonLocked()).isTrue();
	}

	@Test
	@DisplayName("processLoginFailure로 로그인 실패 처리 시, 실패 횟수가 5회 이상이면 계정이 잠긴다.")
	void should_lockAccount_when_processLoginFailure() {
		// given
		Member member = Member.builder()
			.username("testUser")
			.password("encodedPassword")
			.email("test@test.com")
			.role(Role.USER)
			.loginFailureCount(4)
			.build();

		// when
		memberDomainService.processLoginFailure(member);

		// then
		assertThat(member.getLoginFailureCount()).isEqualTo(5);
		assertThat(member.isAccountNonLocked()).isFalse();
		assertThat(member.getLockReleaseTime()).isAfter(LocalDateTime.now());
	}

	@Test
	@DisplayName("processLoginSuccess로 로그인 성공 처리 시, 계정이 잠겼다면 계정이 잠금 해제되고 마지막 로그인 시간이 갱신된다.")
	void should_unlockAccountAndSetLastLoginTime_when_processLoginSuccess() {
		// given
		Member member = Member.builder()
			.username("testUser")
			.password("encodedPassword")
			.email("test@test.com")
			.role(Role.USER)
			.accountNonLocked(false)
			.build();

		// when
		memberDomainService.processLoginSuccess(member);

		// then
		assertThat(member.isAccountNonLocked()).isTrue();
		assertThat(member.getLastLoginAt()).isNotNull();
	}

	@Test
	@DisplayName("isSamePassword로 비밀번호가 같은지 확인 시, 비밀번호가 같으면 true를 반환한다.")
	void should_returnTrue_when_isSamePassword() {
		// given
		String newPassword = "newPassword$123";
		String password = "password$123";

		// when
		boolean result = memberDomainService.isSamePassword(newPassword, password);

		// then
		assertThat(result).isFalse();
	}
}
