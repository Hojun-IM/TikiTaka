package com.trillion.tikitaka.domain.category.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubCategoryResponse {
	private Long secondaryCategoryId;
	private String secondaryCategoryName;

	@QueryProjection
	public SubCategoryResponse(Long secondaryCategoryId, String secondaryCategoryName) {
		this.secondaryCategoryId = secondaryCategoryId;
		this.secondaryCategoryName = secondaryCategoryName;
	}
}
