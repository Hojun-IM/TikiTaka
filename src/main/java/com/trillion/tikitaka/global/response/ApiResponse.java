package com.trillion.tikitaka.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

	private Boolean success;
	private String message;
	private T data;

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, "요청에 성공했습니다", data);
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(true, message, data);
	}
}
