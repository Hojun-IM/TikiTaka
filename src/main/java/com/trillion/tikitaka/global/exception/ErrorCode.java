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
	NEW_PASSWORD_NOT_CHANGED(HttpStatus.BAD_REQUEST, "MEM_002", "현재 비밀번호와 새 비밀번호가 동일합니다."),
	CURRENT_PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "MEM_003", "현재 비밀번호가 일치하지 않습니다."),
	CANNOT_DELETE_MYSELF(HttpStatus.BAD_REQUEST, "MEM_004", "자신의 계정을 삭제할 수 없습니다."),
	REGISTRATION_NOT_FOUND(HttpStatus.NOT_FOUND, "REG_001", "등록 정보를 찾을 수 없습니다."),
	REGISTRATION_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "REG_002", "이미 처리된 등록 정보입니다."),
	DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "REG_003", "이미 사용 중인 아이디입니다."),
	DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "REG_004", "이미 사용 중인 이메일입니다."),
	TICKET_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "SUP_001", "티켓 유형을 찾을 수 없습니다."),
	DUPLICATED_TICKET_TYPE_NAME(HttpStatus.BAD_REQUEST, "SUP_002", "이미 존재하는 티켓 유형 이름입니다."),
	CANNOT_HANDLE_DEFAULT_TICKET_TYPE(HttpStatus.BAD_REQUEST, "SUP_003", "기본 티켓 유형은 수정, 삭제할 수 없습니다."),
	CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "SUP_004", "카테고리를 찾을 수 없습니다."),
	PRIMARY_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "SUP_005", "1차 카테고리를 찾을 수 없습니다."),
	DUPLICATED_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "SUP_006", "이미 존재하는 카테고리 이름입니다.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String errorCode, String message) {
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
		this.message = message;
	}
}
