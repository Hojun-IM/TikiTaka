package com.trillion.tikitaka.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.trillion.tikitaka.global.response.ErrorResponse;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(BusinessException exp) {
		log.error("[예외 발생] : {}, {}, {}",
			exp.getErrorCode().getHttpStatus(), exp.getErrorCode().getErrorCode(), exp.getErrorCode().getMessage());
		ErrorResponse errorResponse = new ErrorResponse(
			exp.getErrorCode().getHttpStatus(),
			exp.getErrorCode().getErrorCode(),
			exp.getErrorCode().getMessage()
		);
		return new ResponseEntity<>(errorResponse, exp.getErrorCode().getHttpStatus());
	}

	/**
	 * 바디로 들어온 JSON 값이 잘못된 경우 발생하는 예외 처리
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
		log.error("[예외 발생] : {}, {}, {}", ErrorCode.INVALID_REQUEST_VALUE.getHttpStatus(),
			ErrorCode.INVALID_REQUEST_VALUE.getErrorCode(), ErrorCode.INVALID_REQUEST_VALUE.getMessage());
		String errorMessage = exp.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(fieldError -> String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()))
			.findFirst()
			.orElse(ErrorCode.INVALID_REQUEST_VALUE.getMessage());

		ErrorResponse errorResponse = new ErrorResponse(
			ErrorCode.INVALID_REQUEST_VALUE.getHttpStatus(),
			ErrorCode.INVALID_REQUEST_VALUE.getErrorCode(),
			errorMessage
		);

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({OptimisticLockException.class, ObjectOptimisticLockingFailureException.class})
	public ResponseEntity<ErrorResponse> handleOptimisticLockException(Exception exp) {
		log.error("[예외 발생] : {}, {}, {}", ErrorCode.OPTIMISTIC_LOCK_ACTIVE.getHttpStatus(),
			ErrorCode.OPTIMISTIC_LOCK_ACTIVE.getErrorCode(), ErrorCode.OPTIMISTIC_LOCK_ACTIVE.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(
			ErrorCode.OPTIMISTIC_LOCK_ACTIVE.getHttpStatus(),
			ErrorCode.OPTIMISTIC_LOCK_ACTIVE.getErrorCode(),
			ErrorCode.OPTIMISTIC_LOCK_ACTIVE.getMessage()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception exp) {
		log.error("[예외 발생] : {}, {}, {}", ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus(),
			ErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(
			ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus(),
			ErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
			ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
