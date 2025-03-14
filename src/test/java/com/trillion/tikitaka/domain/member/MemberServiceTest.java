package com.trillion.tikitaka.domain.member;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.trillion.tikitaka.domain.member.application.MemberService;
import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.domain.member.domain.MemberDomainService;
import com.trillion.tikitaka.domain.member.domain.Role;
import com.trillion.tikitaka.domain.member.dto.CreateMemberResult;
import com.trillion.tikitaka.domain.member.dto.MemberInfoListResponse;
import com.trillion.tikitaka.domain.member.dto.MemberInfoResponse;
import com.trillion.tikitaka.domain.member.dto.PasswordChangeRequest;
import com.trillion.tikitaka.domain.member.infrastructure.MemberRepository;
import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;
import com.trillion.tikitaka.global.security.domain.CustomUserDetails;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private MemberDomainService memberDomainService;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private MemberService memberService;

	@Nested
	@DisplayName("getMyInfo 호출 시")
	class GetMyInfoTest {

		@Test
		@DisplayName("정상적으로 내 정보를 조회한다.")
		void should_returnMyInfo_when_userDetailsIsValid() {
			// given
			Long memberId = 1L;
			CustomUserDetails userDetails = createMockUserDetailsWithId(memberId);

			MemberInfoResponse mockResponse = new MemberInfoResponse(
				userDetails.getMember().getId(),
				userDetails.getMember().getUsername(),
				userDetails.getMember().getEmail(),
				userDetails.getMember().getRole(),
				null
			);
			given(memberRepository.getMemberInfo(memberId)).willReturn(mockResponse);

			// when
			MemberInfoResponse result = memberService.getMyInfo(userDetails);

			// then
			assertThat(result.getMemberId()).isEqualTo(1L);
			assertThat(result.getUsername()).isEqualTo("testUser");
			assertThat(result.getEmail()).isEqualTo("test@test.com");
			assertThat(result.getRole()).isEqualTo(Role.USER);
		}
	}

	@Nested
	@DisplayName("getAllMembersForAdmin 호출 시")
	class GetAllMembersForAdminTest {

		@Test
		@DisplayName("역할 필터링이 주어지지 않으면, 모든 사용자를 조회하고 전체 역할별 수를 반환한다.")
		void should_returnAllMembers_when_roleIsNull() {
			// given
			PageRequest pageable = PageRequest.of(0, 10);
			Role role = null;

			MemberInfoListResponse mockResponse = new MemberInfoListResponse();
			mockResponse.setMemberInfo(new PageImpl<>(Collections.emptyList()));
			mockResponse.setRoleCount(5L, 10L, 20L);

			given(memberRepository.getAllMembersForAdminByRole(pageable, role)).willReturn(mockResponse);

			// when
			MemberInfoListResponse result = memberService.getAllMembersForAdmin(pageable, role);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getMemberInfo()).isEmpty();
			assertThat(result.getAdminCount()).isEqualTo(5L);
			assertThat(result.getManagerCount()).isEqualTo(10L);
			assertThat(result.getUserCount()).isEqualTo(20L);
		}

		@Test
		@DisplayName("역할이 주어지면, 해당 역할만 페이징 조회하고 전체 역할별 수를 반환한다.")
		void should_returnManagerMembers_when_roleIsManager() {
			// given
			PageRequest pageable = PageRequest.of(1, 5);
			Role role = Role.MANAGER;

			MemberInfoListResponse mockResponse = new MemberInfoListResponse();
			mockResponse.setMemberInfo(new PageImpl<>(Collections.emptyList()));
			mockResponse.setRoleCount(5L, 10L, 20L);

			given(memberRepository.getAllMembersForAdminByRole(pageable, role)).willReturn(mockResponse);

			// when
			MemberInfoListResponse result = memberService.getAllMembersForAdmin(pageable, role);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getMemberInfo()).isEmpty();
			assertThat(result.getAdminCount()).isEqualTo(5L);
			assertThat(result.getManagerCount()).isEqualTo(10L);
			assertThat(result.getUserCount()).isEqualTo(20L);
		}
	}

	@Nested
	@DisplayName("getAllMembersForManagerAndUser 호출 시")
	class GetAllMembersForManagerAndUserTest {

		@Test
		@DisplayName("관리자 역할을 조회하려고 하면, 예외를 발생시킨다.")
		void should_throwException_when_roleIsAdmin() {
			// given
			Role role = Role.ADMIN;

			// when & then
			BusinessException ex = assertThrows(BusinessException.class,
				() -> memberService.getAllMembersForManagerAndUser(role));
			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED);
		}

		@Test
		@DisplayName("관리자 이외의 역할을 조회하면, 해당 역할의 사용자 목록을 반환한다.")
		void should_returnMembers_when_roleIsNotAdmin() {
			// given
			Role role = Role.USER;
			MemberInfoResponse mockInfo = new MemberInfoResponse(
				10L, "mockUser", "mockUser@test.com", Role.USER, "url"
			);

			given(memberRepository.getAllMembersForManagerAndUser(role))
				.willReturn(List.of(mockInfo));

			// when
			List<MemberInfoResponse> result = memberService.getAllMembersForManagerAndUser(role);

			// then
			assertThat(result).hasSize(1);
			assertThat(result.get(0).getRole()).isEqualTo(Role.USER);
		}
	}

	@Nested
	@DisplayName("getMemberInfo 호출 시")
	class GetMemberInfoTest {

		@Test
		@DisplayName("해당 회원 ID가 없다면, 예외를 발생시킨다.")
		void should_throwException_when_memberIsNotFound() {
			// given
			Long targetMemberId = 20L;
			CustomUserDetails requester = createMockUserDetailsWithId(1L);

			given(memberRepository.findById(targetMemberId)).willReturn(Optional.empty());

			// when & then
			BusinessException ex = assertThrows(BusinessException.class,
				() -> memberService.getMemberInfo(targetMemberId, requester));
			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
		}

		@Test
		@DisplayName("만약 요청자가 관리자가 아니고, 조회 대상이 관리자면 예외가 발생한다.")
		void should_throwException_when_requesterIsNotAdminAndTargetIsAdmin() {
			// given
			Long targetAdminId = 30L;
			CustomUserDetails requester = createMockUserDetailsWithIdAndRole(1L, Role.USER);

			Member adminMember = Member.builder()
				.username("admin")
				.email("admin@test.com")
				.password("adminPwd$123")
				.role(Role.ADMIN)
				.build();
			ReflectionTestUtils.setField(adminMember, "id", targetAdminId);

			given(memberRepository.findById(targetAdminId)).willReturn(Optional.of(adminMember));

			// when & then
			BusinessException ex = assertThrows(BusinessException.class,
				() -> memberService.getMemberInfo(targetAdminId, requester));
			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED);
		}

		@Test
		@DisplayName("정상적인 요청이라면, 사용자 정보를 조회한다.")
		void should_returnMemberInfo_when_requestIsValid() {
			// given
			Long targetMemberId = 40L;
			CustomUserDetails requester = createMockUserDetailsWithIdAndRole(10L, Role.ADMIN);

			Member normalMember = Member.builder()
				.username("member")
				.email("member@test.com")
				.password("memberPwd$123")
				.role(Role.USER)
				.build();
			ReflectionTestUtils.setField(normalMember, "id", targetMemberId);

			given(memberRepository.findById(targetMemberId)).willReturn(Optional.of(normalMember));

			MemberInfoResponse mockInfo = new MemberInfoResponse(
				targetMemberId, "member", "member@test.com", Role.USER, "url"
			);
			given(memberRepository.getMemberInfo(targetMemberId)).willReturn(mockInfo);

			// when
			MemberInfoResponse result = memberService.getMemberInfo(targetMemberId, requester);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getMemberId()).isEqualTo(targetMemberId);
			assertThat(result.getRole()).isEqualTo(Role.USER);
		}
	}

	@Nested
	@DisplayName("changePassword 호출 시")
	class ChangePasswordTest {

		@Test
		@DisplayName("요청한 사용자를 찾을 수 없으면, 예외가 발생한다.")
		void should_throwException_when_memberIsNotFound() {
			// given
			CustomUserDetails userDetails = createMockUserDetailsWithId(1L);
			PasswordChangeRequest request = new PasswordChangeRequest("pass$123", "newPass$123");

			given(memberRepository.findById(1L)).willReturn(Optional.empty());

			// when & then
			BusinessException ex = assertThrows(BusinessException.class,
				() -> memberService.changePassword(userDetails, request));
			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
		}

		@Test
		@DisplayName("현재 비밀번호가 일치하지 않으면, 예외가 발생한다.")
		void should_throwException_when_currentPasswordIsNotMatched() {
			// given
			CustomUserDetails userDetails = createMockUserDetailsWithId(1L);
			PasswordChangeRequest request = new PasswordChangeRequest("wrongPass$123", "newPass$123");

			Member member = userDetails.getMember();
			given(memberRepository.findById(1L)).willReturn(Optional.of(member));
			given(passwordEncoder.matches("wrongPass$123", member.getPassword())).willReturn(false);

			// when & then
			BusinessException ex = assertThrows(BusinessException.class,
				() -> memberService.changePassword(userDetails, request));
			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.CURRENT_PASSWORD_NOT_MATCHED);
		}

		@Test
		@DisplayName("새 비밀번호가 기존 비밀번호와 동일하면, 예외가 발생한다.")
		void should_throwException_when_newPasswordIsSameAsCurrent() {
			// given
			CustomUserDetails userDetails = createMockUserDetailsWithId(1L);
			PasswordChangeRequest request = new PasswordChangeRequest("pass$123", "pass$123");

			Member member = userDetails.getMember();
			given(memberRepository.findById(1L)).willReturn(Optional.of(member));
			given(passwordEncoder.matches("pass$123", member.getPassword())).willReturn(true);
			given(memberDomainService.isSamePassword("pass$123", "pass$123")).willReturn(true);

			// when & then
			BusinessException ex = assertThrows(BusinessException.class,
				() -> memberService.changePassword(userDetails, request));
			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NEW_PASSWORD_NOT_CHANGED);
		}

		@Test
		@DisplayName("정상적인 요청이라면, 비밀번호를 변경한다.")
		void should_updatePassword_when_requestIsValid() {
			// given
			CustomUserDetails userDetails = createMockUserDetailsWithId(1L);
			PasswordChangeRequest request = new PasswordChangeRequest("pass$123", "newPass$123");

			Member member = userDetails.getMember();
			given(memberRepository.findById(1L)).willReturn(Optional.of(member));
			given(passwordEncoder.matches("pass$123", member.getPassword())).willReturn(true);
			given(memberDomainService.isSamePassword("pass$123", "newPass$123")).willReturn(false);
			given(passwordEncoder.encode("newPass$123")).willReturn("encodedNewPass$123");

			// when
			memberService.changePassword(userDetails, request);

			// then
			then(memberDomainService).should(times(1))
				.updatePassword(member, "encodedNewPass$123");
		}
	}

	@Nested
	@DisplayName("deleteMember 호출 시")
	class DeleteMemberTest {

		@Test
		@DisplayName("요청한 사용자를 찾을 수 없으면, 예외가 발생한다.")
		void should_throwException_when_memberIsNotFound() {
			// given
			Long memberId = 1L;
			CustomUserDetails userDetails = createMockUserDetailsWithId(memberId);

			given(memberRepository.findById(memberId)).willReturn(Optional.empty());

			// when & then
			BusinessException ex = assertThrows(BusinessException.class,
				() -> memberService.deleteMember(memberId, userDetails));
			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
		}

		@Test
		@DisplayName("본인 계정 삭제 시 예외가 발생한다.")
		void should_throwException_when_deleteMyAccount() {
			// given
			Long memberId = 1L;
			CustomUserDetails userDetails = createMockUserDetailsWithId(memberId);

			Member member = userDetails.getMember();
			given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

			// when & then
			assertThrows(BusinessException.class, () -> {
				memberService.deleteMember(memberId, userDetails);
			});
		}

		@Test
		@DisplayName("정상적인 요청이라면, 사용자를 삭제한다.")
		void should_deleteMember_when_requestIsValid() {
			// given
			Long memberId = 1L;
			CustomUserDetails userDetails = createMockUserDetailsWithId(10L);

			Member member = Member.builder()
				.username("member")
				.email("member@test.com")
				.password("memberPwd$123")
				.role(Role.USER)
				.build();
			ReflectionTestUtils.setField(member, "id", memberId);

			given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

			// when
			memberService.deleteMember(memberId, userDetails);

			// then
			then(memberRepository).should(times(1)).delete(member);
		}
	}

	@Nested
	@DisplayName("changeMemberRole 호출 시")
	class ChangeMemberRoleTest {

		@Test
		@DisplayName("요청한 사용자를 찾을 수 없으면, 예외가 발생한다.")
		void should_throwException_when_memberIsNotFound() {
			// given
			Long memberId = 1L;
			Role role = Role.ADMIN;
			CustomUserDetails userDetails = createMockUserDetailsWithId(10L);

			given(memberRepository.findById(memberId)).willReturn(Optional.empty());

			// when & then
			BusinessException ex = assertThrows(BusinessException.class,
				() -> memberService.changeMemberRole(memberId, role, userDetails));
			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
		}

		@Test
		@DisplayName("본인 계정 역할 변경 시 예외가 발생한다.")
		void should_throwException_when_changeRoleOfMyAccount() {
			// given
			Long memberId = 1L;
			Role role = Role.ADMIN;
			CustomUserDetails userDetails = createMockUserDetailsWithId(memberId);

			Member member = userDetails.getMember();
			given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

			// when & then
			assertThrows(BusinessException.class, () -> {
				memberService.changeMemberRole(memberId, role, userDetails);
			});
		}

		@Test
		@DisplayName("정상적인 요청이라면, 사용자의 역할을 변경한다.")
		void should_changeRole_when_requestIsValid() {
			// given
			Long memberId = 1L;
			Role role = Role.ADMIN;
			CustomUserDetails userDetails = createMockUserDetailsWithId(10L);

			Member member = Member.builder()
				.username("member")
				.email("member@test.com")
				.password("memberPwd$123")
				.role(Role.USER)
				.build();
			ReflectionTestUtils.setField(member, "id", memberId);

			given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

			// when
			memberService.changeMemberRole(memberId, role, userDetails);

			// then
			then(memberDomainService).should(times(1))
				.updateRole(member, role);
		}
	}

	@Nested
	@DisplayName("createMember 호출 시")
	class CreateMemberTest {

		@Test
		@DisplayName("랜덤 비밀번호를 할당한 후, CreateMemberResult를 반환한다.")
		void should_returnCreateMemberResult_when_createMember() {
			// given
			String username = "testUser";
			String email = "test@test.com";
			Role role = Role.USER;
			String encodedPassword = "encodedPwd$123";

			given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);
			given(memberDomainService.createMember(username, email, role, encodedPassword))
				.willReturn(
					Member.builder()
						.username(username)
						.email(email)
						.password(encodedPassword)
						.role(role)
						.build()
				);
			given(memberRepository.save(any(Member.class))).willAnswer(invocation -> invocation.getArgument(0));

			// when
			CreateMemberResult result = memberService.createMember(username, email, role);

			// then
			assertAll(
				() -> assertThat(result).isNotNull(),
				() -> assertThat(result.getNewMember().getUsername()).isEqualTo(username),
				() -> assertThat(result.getNewMember().getEmail()).isEqualTo(email),
				() -> assertThat(result.getNewMember().getRole()).isEqualTo(Role.USER),
				() -> assertThat(result.getCreatedPassword()).isNotBlank()
			);
		}
	}

	private CustomUserDetails createMockUserDetailsWithId(Long memberId) {
		return createMockUserDetailsWithIdAndRole(memberId, Role.USER);
	}

	private CustomUserDetails createMockUserDetailsWithIdAndRole(Long memberId, Role role) {
		Member member = Member.builder()
			.username("testUser")
			.password("encodedPassword")
			.email("test@test.com")
			.role(role)
			.build();
		ReflectionTestUtils.setField(member, "id", memberId);
		return new CustomUserDetails(member);
	}
}
