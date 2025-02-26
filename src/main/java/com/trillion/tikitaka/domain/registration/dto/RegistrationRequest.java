package com.trillion.tikitaka.domain.registration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

	@NotBlank
	@Size(min = 4, max = 30)
	@Pattern(
		regexp = "^[a-z]{3,10}\\.[a-z]{1,5}$",
		message = "아이디는 'aaa.bbb' 형식으로 3~10자의 소문자 + '.' + 1~5자의 소문자로 입력하세요."
	)
	private String username;

	@NotBlank
	@Email
	private String email;
}
