package com.trillion.tikitaka.domain.registration.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trillion.tikitaka.domain.member.application.MemberService;
import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.domain.member.infrastructure.MemberRepository;
import com.trillion.tikitaka.domain.registration.domain.Registration;
import com.trillion.tikitaka.domain.registration.domain.RegistrationDomainService;
import com.trillion.tikitaka.domain.registration.domain.RegistrationStatus;
import com.trillion.tikitaka.domain.registration.dto.RegistrationListResponse;
import com.trillion.tikitaka.domain.registration.dto.RegistrationProcessRequest;
import com.trillion.tikitaka.domain.registration.dto.RegistrationRequest;
import com.trillion.tikitaka.domain.registration.infrastructure.RegistrationRepository;
import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RegistrationService {

	private final MemberRepository memberRepository;
	private final RegistrationRepository registrationRepository;
	private final RegistrationDomainService registrationDomainService;
	private final MemberService memberService;

	@Transactional
	public void requestRegistration(RegistrationRequest request) {
		log.info("[계정 등록 신청] 아이디: {}, 이메일: {}", request.getUsername(), request.getEmail());

		validateDuplicateRegistration(request.getUsername(), request.getEmail());

		Registration registration = registrationDomainService.createRegistration(
			request.getUsername(),
			request.getEmail()
		);
		registrationRepository.save(registration);
	}

	public Page<RegistrationListResponse> getRegistrations(RegistrationStatus status, Pageable pageable) {
		log.info("[계정 등록 목록 조회] 상태: {}", status);
		return registrationRepository.getRegistrations(status, pageable);
	}

	@Transactional
	public void processRegistration(Long registrationId, RegistrationProcessRequest request) {
		log.info("[계정 등록 처리] 등록 ID: {}, 상태: {}", registrationId, request.getStatus());

		Registration registration = registrationRepository.findById(registrationId).orElseThrow(() -> {
			log.error("[계정 등록 처리 실패] 존재하지 않는 계정 등록 신청: {}", registrationId);
			return new BusinessException(ErrorCode.REGISTRATION_NOT_FOUND);
		});

		if (registration.getStatus() != RegistrationStatus.PENDING) {
			log.error("[계정 등록 처리 실패] 이미 처리된 계정 등록 신청: {}", registrationId);
			throw new BusinessException(ErrorCode.REGISTRATION_ALREADY_PROCESSED);
		}

		String message;
		if (request.getStatus() == RegistrationStatus.APPROVED) {
			message = registrationDomainService.approveRegistration(registration, request.getReason());
			Member newMember = memberService.createMember(
				registration.getUsername(), registration.getEmail(), request.getRole()
			);
			message += " (임시 비밀번호 발급됨)";
		} else if (request.getStatus() == RegistrationStatus.REJECTED) {
			message = registrationDomainService.rejectRegistration(registration, request.getReason());
		} else {
			log.error("[계정 등록 처리 실패] 유효하지 않은 상태: {}", request.getStatus());
			throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		// TODO: 계정 등록 처리 알림 전송
	}

	/**
	 * 중복된 계정 등록 신청인지 확인한다.
	 *
	 * 중복 사항:
	 * 1. Member에 중복된 아이디 또는 이메일이 존재한다.
	 * 2. Registration에 중복된 아이디 또는 이메일이 PENDING 상태로 존재한다.
	 * 3. Registration에 중복된 아이디 또는 이메일이 APPROVED 상태로 존재하고, Member에 삭제되지 않은 계정이 존재한다.
	 */
	private void validateDuplicateRegistration(String username, String email) {
		if (memberRepository.existsByUsername(username)) {
			log.error("[계정 등록 실패] 중복된 아이디: {}", username);
			throw new BusinessException(ErrorCode.DUPLICATED_USERNAME);
		}
		if (memberRepository.existsByEmail(email)) {
			log.error("[계정 등록 실패] 중복된 이메일: {}", email);
			throw new BusinessException(ErrorCode.DUPLICATED_EMAIL);
		}
		if (registrationRepository.existsByUsernameAndStatus(username, RegistrationStatus.PENDING)) {
			log.error("[계정 등록 실패] 이미 등록 신청된 아이디: {}", username);
			throw new BusinessException(ErrorCode.DUPLICATED_USERNAME);
		}
		if (registrationRepository.existsByEmailAndStatus(email, RegistrationStatus.PENDING)) {
			log.error("[계정 등록 실패] 이미 등록 신청된 이메일: {}", email);
			throw new BusinessException(ErrorCode.DUPLICATED_EMAIL);
		}

		boolean approvedExists = registrationRepository.existsByUsernameAndStatus(username, RegistrationStatus.APPROVED)
			|| registrationRepository.existsByEmailAndStatus(email, RegistrationStatus.APPROVED);

		if (approvedExists && (memberRepository.existsByUsernameAndDeletedAtIsNull(username)
			|| memberRepository.existsByEmailAndDeletedAtIsNull(email))) {
			log.error("[계정 등록 실패] 기존 승인된 등록 및 사용자 존재: {} / {}", username, email);
			throw new BusinessException(ErrorCode.DUPLICATED_USERNAME);
		}
	}
}
