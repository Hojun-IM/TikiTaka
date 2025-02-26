package com.trillion.tikitaka.domain.registration.presentation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trillion.tikitaka.domain.registration.application.RegistrationService;
import com.trillion.tikitaka.domain.registration.domain.RegistrationStatus;
import com.trillion.tikitaka.domain.registration.dto.RegistrationListResponse;
import com.trillion.tikitaka.domain.registration.dto.RegistrationProcessRequest;
import com.trillion.tikitaka.domain.registration.dto.RegistrationRequest;
import com.trillion.tikitaka.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {

	private final RegistrationService registrationService;

	@PostMapping("/registrations")
	public ApiResponse<Void> requestRegistration(@RequestBody @Valid RegistrationRequest request) {
		registrationService.requestRegistration(request);
		return ApiResponse.success(null);
	}

	@GetMapping("/admin/registrations")
	public ApiResponse<Page<RegistrationListResponse>> getRegistrationList(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "20") int size,
		@RequestParam(value = "status", required = false) RegistrationStatus status
	) {
		Pageable pageable = PageRequest.of(page, size);
		Page<RegistrationListResponse> registrations = registrationService.getRegistrations(status, pageable);

		return ApiResponse.success(registrations);
	}

	@PostMapping("/admin/registrations/{registrationId}")
	public ApiResponse<Void> processRegistration(
		@PathVariable("registrationId") Long registrationId,
		@RequestBody @Valid RegistrationProcessRequest request
	) {
		registrationService.processRegistration(registrationId, request);
		return ApiResponse.success(null);
	}
}
