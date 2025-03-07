package com.trillion.tikitaka.domain.registration.domain;

import org.springframework.stereotype.Service;

import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;

@Service
public class RegistrationDomainService {

	public Registration createRegistration(String username, String email) {
		return Registration.builder()
			.username(username)
			.email(email)
			.status(RegistrationStatus.PENDING)
			.build();
	}

	public String approveRegistration(Registration registration, String reason) {
		if (registration.getStatus() != RegistrationStatus.PENDING) {
			throw new BusinessException(ErrorCode.REGISTRATION_ALREADY_PROCESSED);
		}
		registration.approve(reason);
		return reason;
	}

	public String rejectRegistration(Registration registration, String reason) {
		if (registration.getStatus() != RegistrationStatus.PENDING) {
			throw new BusinessException(ErrorCode.REGISTRATION_ALREADY_PROCESSED);
		}
		registration.reject(reason);
		return reason;
	}
}
