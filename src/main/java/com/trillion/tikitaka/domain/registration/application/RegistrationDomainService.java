package com.trillion.tikitaka.domain.registration.application;

import org.springframework.stereotype.Service;

import com.trillion.tikitaka.domain.registration.domain.Registration;
import com.trillion.tikitaka.domain.registration.domain.RegistrationStatus;
import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationDomainService {

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
