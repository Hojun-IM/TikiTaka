package com.trillion.tikitaka.global.security.filter;

import static com.trillion.tikitaka.global.security.constant.AuthenticationConstants.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.domain.member.domain.MemberDomainService;
import com.trillion.tikitaka.domain.member.infrastructure.MemberRepository;
import com.trillion.tikitaka.global.security.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

	private final MemberRepository memberRepository;
	private final MemberDomainService memberDomainService;

	@Override
	@Transactional
	protected void additionalAuthenticationChecks(
		UserDetails userDetails,
		UsernamePasswordAuthenticationToken authToken
	) throws AuthenticationException {

		CustomUserDetails customUserDetails = (CustomUserDetails)userDetails;
		Member member = customUserDetails.getMember();

		checkAccountLockStatus(member);

		try {
			super.additionalAuthenticationChecks(userDetails, authToken);
		} catch (BadCredentialsException ex) {
			memberDomainService.processLoginFailure(member);
			memberRepository.save(member);

			int failureCount = member.getLoginFailureCount();
			log.error("[인증] 로그인 실패: {}/{}", failureCount, MAX_LOGIN_FAILURE_COUNT);

			throw new BadCredentialsException(
				"아이디 또는 비밀번호가 일치하지 않습니다. (" + failureCount + "/" + MAX_LOGIN_FAILURE_COUNT + ")"
			);
		}

		memberDomainService.processLoginSuccess(member);
		memberRepository.save(member);
	}

	private void checkAccountLockStatus(Member member) {
		if (!member.isAccountNonLocked()) {
			if (member.getLockReleaseTime() != null && LocalDateTime.now().isBefore(member.getLockReleaseTime())) {
				long minutesLeft = ChronoUnit.MINUTES.between(LocalDateTime.now(), member.getLockReleaseTime());
				log.error("[인증] 계정이 잠겼습니다. 남은 시간: {}분", minutesLeft);
				throw new LockedException("계정이 잠겼습니다. 남은 시간: " + minutesLeft + "분");
			} else {
				memberDomainService.resetLoginFailCount(member);
				memberRepository.saveAndFlush(member);
			}
		}
	}
}
