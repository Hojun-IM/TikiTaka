package com.trillion.tikitaka.domain.registration.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import com.trillion.tikitaka.domain.registration.domain.RegistrationStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegistrationListResponse {

	private Long registrationId;
	private String username;
	private String email;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private RegistrationStatus status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	@QueryProjection
	public RegistrationListResponse(Long registrationId, String username, String email, RegistrationStatus status,
		LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.registrationId = registrationId;
		this.username = username;
		this.email = email;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
