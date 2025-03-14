package com.trillion.tikitaka.domain.member;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.domain.member.domain.Role;

/**
 * 테스트 주체: Member
 * 협력 객체: 없음
 */
@DisplayName("Member 테스트")
public class MemberTest {

	@Test
	@DisplayName("로그인 실패 횟수 증가 시, 기존 횟수에서 +1이 되어야 한다.")
	void should_increaseByOne_when_increaseLoginFailureCount() {
		// given
		Member member = Member.builder()
			.username("testUser")
			.password("testPass$123")
			.email("test@test.com")
			.role(Role.USER)
			.loginFailureCount(0)
			.build();

		// when
		member.increaseLoginFailureCount();

		// then
		assertThat(member.getLoginFailureCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("로그인 실패 횟수 초기화 시, 0이 되어야 한다.")
	void should_setCountToZero_when_resetLoginFailCount() {
		// given
		Member member = Member.builder()
			.username("testUser")
			.password("testPass$123")
			.email("test@test.com")
			.role(Role.USER)
			.loginFailureCount(1)
			.build();

		// when
		member.resetLoginFailCount();

		// then
		assertThat(member.getLoginFailureCount()).isZero();
	}

	@Test
	@DisplayName("계정 잠금 시, 계정 활성화가 false로 설정되고 잠금 해제 시간이 설정되어야 한다.")
	void should_setAccountNonLockedToFalseAndSetLockReleaseTime_when_lockAccount() {
		// given
		Member member = Member.builder()
			.username("testUser")
			.password("testPass$123")
			.email("test@test.com")
			.role(Role.USER)
			.build();
		LocalDateTime lockExpireTime = LocalDateTime.now().plusMinutes(30);

		// when
		member.lockAccount(lockExpireTime);

		// then
		assertThat(member.isAccountNonLocked()).isFalse();
		assertThat(member.getLockReleaseTime()).isEqualTo(lockExpireTime);
	}

	@Test
	@DisplayName("계정 잠금 해제 시, 계정 활성화가 true로 설정되고 잠금 해제 시간이 null로 설정되어야 한다.")
	void should_setAccountNonLockedToTrueAndSetLockReleaseTimeToNull_when_unlockAccount() {
		// given
		Member member = Member.builder()
			.username("testUser")
			.password("testPass$123")
			.email("test@test.com")
			.role(Role.USER)
			.accountNonLocked(false)
			.lockReleaseTime(LocalDateTime.now().plusHours(1))
			.build();

		// when
		member.unlockAccount();

		// then
		assertThat(member.isAccountNonLocked()).isTrue();
		assertThat(member.getLockReleaseTime()).isNull();
	}

	@Test
	@DisplayName("비밀번호 변경 시, 새로운 비밀번호로 변경되고 마지막 비밀번호 변경 시간이 업데이트되어야 한다.")
	void should_updatePasswordAndSetLastPasswordChangedAt_when_updatePassword() {
		// given
		Member member = Member.builder()
			.username("testUser")
			.password("testPass$123")
			.email("test@test.com")
			.role(Role.USER)
			.build();

		// when
		member.updatePassword("newPass$123");

		// then
		assertThat(member.getPassword()).isEqualTo("newPass$123");
		assertThat(member.getLastPasswordChangedAt()).isNotNull();
	}

	@Test
	@DisplayName("마지막 로그인 시간 업데이트 시, 마지막 로그인 시간이 업데이트되어야 한다.")
	void should_updateLastLoginAt_when_updateLastLoginAt() {
		// given
		Member member = Member.builder()
			.username("testUser")
			.password("testPass$123")
			.email("test@test.com")
			.role(Role.USER)
			.build();

		// when
		LocalDateTime now = LocalDateTime.now();
		member.updateLastLoginAt(now);

		// then
		assertThat(member.getLastLoginAt()).isEqualTo(now);
	}

	@Test
	@DisplayName("역할 변경 시, 새로운 역할로 변경되어야 한다.")
	void should_updateRole_when_updateRole() {
		// given
		Member member = Member.builder()
			.username("testUser")
			.password("testPass$123")
			.email("test@test.com")
			.role(Role.USER)
			.build();

		// when
		member.updateRole(Role.ADMIN);

		// then
		assertThat(member.getRole()).isEqualTo(Role.ADMIN);
	}
}

