package com.trillion.tikitaka.domain.registration.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.trillion.tikitaka.domain.registration.domain.RegistrationStatus;
import com.trillion.tikitaka.domain.registration.dto.RegistrationListResponse;

public interface CustomRegistrationRepository {

	Page<RegistrationListResponse> getRegistrations(RegistrationStatus status, Pageable pageable);
}
