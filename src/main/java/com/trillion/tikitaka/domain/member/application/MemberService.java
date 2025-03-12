package com.trillion.tikitaka.domain.member.application;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.domain.member.domain.MemberDomainService;
import com.trillion.tikitaka.domain.member.domain.Role;
import com.trillion.tikitaka.domain.member.dto.CreateMemberResult;
import com.trillion.tikitaka.domain.member.dto.MemberInfoListResponse;
import com.trillion.tikitaka.domain.member.dto.MemberInfoResponse;
import com.trillion.tikitaka.domain.member.dto.PasswordChangeRequest;
import com.trillion.tikitaka.domain.member.infrastructure.MemberRepository;
import com.trillion.tikitaka.domain.registration.application.PasswordGenerator;
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

	private final PasswordEncoder passwordEncoder;
	private final MemberDomainService memberDomainService;
	private final MemberRepository memberRepository;

	public MemberInfoResponse getMyInfo(CustomUserDetails userDetails) {
		log.info("[내 정보 조회] 사용자 아이디: {}", userDetails.getMember().getUsername());
		return memberRepository.getMemberInfo(userDetails.getMember().getId());
	}

	public MemberInfoListResponse getAllMembersForAdmin(Pageable pageable, Role role) {
		log.info("[관리자용 전체 멤버 조회] 페이지: {}, 사이즈: {}, 역할: {}", pageable.getPageNumber(), pageable.getPageSize(), role);
		return memberRepository.getAllMembersForAdminByRole(pageable, role);
	}

	public List<MemberInfoResponse> getAllMembersForManagerAndUser(Role role) {
		log.info("[매니저, 사용자용 전체 멤버 조회] 역할: {}", role);

		if (role == Role.ADMIN) {
			log.error("[매니저, 사용자용 전체 멤버 조회] 관리자를 조회할 수 없습니다.");
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}

		return memberRepository.getAllMembersForManagerAndUser(role);
	}

	public MemberInfoResponse getMemberInfo(Long memberId, CustomUserDetails userDetails) {
		log.info("[특정 사용자 조회] 사용자 아이디: {}", memberId);

		Member member = memberRepository.findById(memberId).orElseThrow(() -> {
			log.error("[특정 사용자 조회] 사용자를 찾을 수 없습니다.");
			return new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
		});

		if ((userDetails.getMember().getRole() == Role.USER || userDetails.getMember().getRole() == Role.MANAGER)
			&& member.getRole() == Role.ADMIN) {
			log.error("[특정 사용자 조회] 관리자를 조회할 수 있는 권한이 없습니다.");
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}

		return memberRepository.getMemberInfo(memberId);
	}

	@Transactional
	public void changePassword(CustomUserDetails userDetails, PasswordChangeRequest request) {
		log.info("[비밀번호 변경] 사용자: {}", userDetails.getMember().getUsername());

		Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(() -> {
			log.error("[비밀번호 변경] 사용자를 찾을 수 없습니다.");
			return new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
		});

		if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
			log.error("[비밀번호 변경] 현재 비밀번호가 일치하지 않습니다.");
			throw new BusinessException(ErrorCode.CURRENT_PASSWORD_NOT_MATCHED);
		}

		if (memberDomainService.isSamePassword(request.getCurrentPassword(), request.getNewPassword())) {
			log.error("[비밀번호 변경] 새 비밀번호가 기존 비밀번호와 동일합니다.");
			throw new BusinessException(ErrorCode.NEW_PASSWORD_NOT_CHANGED);
		}

		String encodedPassword = passwordEncoder.encode(request.getNewPassword());
		memberDomainService.updatePassword(member, encodedPassword);
	}

	@Transactional
	public void deleteMember(Long memberId, CustomUserDetails userDetails) {
		log.info("[사용자 삭제] 사용자 아이디: {}", memberId);

		Member member = memberRepository.findById(memberId).orElseThrow(() -> {
			log.error("[사용자 삭제] 사용자를 찾을 수 없습니다.");
			return new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
		});

		if (member.getId().equals(userDetails.getMember().getId())) {
			log.error("[사용자 삭제] 자신의 계정은 삭제할 수 없습니다.");
			throw new BusinessException(ErrorCode.CANNOT_DELETE_MYSELF);
		}

		memberRepository.delete(member);
	}

	@Transactional
	public void changeMemberRole(Long memberId, Role role, CustomUserDetails userDetails) {
		log.info("[사용자 역할 변경] 사용자 아이디: {}, 역할: {}", memberId, role);

		Member member = memberRepository.findById(memberId).orElseThrow(() -> {
			log.error("[사용자 역할 변경] 사용자를 찾을 수 없습니다.");
			return new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
		});

		if (memberId.equals(userDetails.getMember().getId())) {
			log.error("[사용자 역할 변경] 자신의 역할을 변경할 수 없습니다.");
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}

		memberDomainService.updateRole(member, role);
	}

	@Transactional
	public CreateMemberResult createMember(String username, String email, Role role) {
		String password = PasswordGenerator.generateRandomPassword();
		String encodedPassword = passwordEncoder.encode(password);

		Member member = memberDomainService.createMember(username, email, role, encodedPassword);
		memberRepository.save(member);

		return new CreateMemberResult(member, password);
	}
}
