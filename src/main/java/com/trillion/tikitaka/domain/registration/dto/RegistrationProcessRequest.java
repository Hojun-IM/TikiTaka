package com.trillion.tikitaka.domain.registration.dto;

import org.hibernate.validator.constraints.Length;

import com.trillion.tikitaka.domain.member.domain.Role;
import com.trillion.tikitaka.domain.registration.domain.RegistrationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationProcessRequest {

	private RegistrationStatus status;

	private Role role;

	@Length(max = 300, message = "사유는 500자 이하여야 합니다.")
	private String reason;
}
