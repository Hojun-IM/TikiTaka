package com.trillion.tikitaka.domain.category.dto;

import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WholeCategoryResponse {
	private Long primaryCategoryId;
	private String primaryCategoryName;
	private List<SubCategoryResponse> subCategories;

	@QueryProjection
	public WholeCategoryResponse(
		Long primaryCategoryId, String primaryCategoryName, List<SubCategoryResponse> subCategories
	) {
		this.primaryCategoryId = primaryCategoryId;
		this.primaryCategoryName = primaryCategoryName;
		this.subCategories = subCategories;
	}
}
