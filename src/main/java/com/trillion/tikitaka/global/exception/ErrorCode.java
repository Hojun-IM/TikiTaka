package com.trillion.tikitaka.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GEN_001", "서버에 문제가 발생했습니다."),
	INVALID_REQUEST_VALUE(HttpStatus.BAD_REQUEST, "GEN_002", "요청 값이 올바르지 않습니다."),
	OPTIMISTIC_LOCK_ACTIVE(HttpStatus.CONFLICT, "GEN_003", "데이터가 변경되었습니다. 다시 시도해주세요."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_001", "인증되지 않은 사용자입니다."),
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_002", "접근 권한이 없습니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_003", "유효하지 않은 토큰입니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_004", "토큰이 만료되었습니다."),
	INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "AUTH_005", "잘못된 토큰 서명입니다."),
	BLACKLISTED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_006", "블랙리스트에 등록된 토큰입니다."),
	REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH_006", "리프레시 토큰이 존재하지 않습니다."),
	INVALID_USERNAME_OR_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH_007", "잘못된 사용자 이름 또는 비밀번호입니다."),
	ACCOUNT_LOCKED(HttpStatus.BAD_REQUEST, "AUTH_008", "계정이 잠겼습니다."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM_001", "사용자를 찾을 수 없습니다."),
	REGISTRATION_NOT_FOUND(HttpStatus.NOT_FOUND, "REG_001", "등록 정보를 찾을 수 없습니다."),
	REGISTRATION_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "REG_002", "이미 처리된 등록 정보입니다."),
	DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "REG_003", "이미 사용 중인 아이디입니다."),
	DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "REG_004", "이미 사용 중인 이메일입니다.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String errorCode, String message) {
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
		this.message = message;
	}
}
