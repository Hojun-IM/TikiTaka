package com.trillion.tikitaka.domain.category.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

	private Long primaryCategoryId;

	@NotEmpty(message = "카테고리명을 입력해주세요.")
	@Length(max = 25, message = "카테고리명은 25자 이내로 입력해주세요.")
	private String name;
}
