package com.trillion.tikitaka.domain.category.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryListResponse {
	private Long categoryId;
	private Long primaryCategoryId;
	private String categoryName;

	@QueryProjection
	public CategoryListResponse(Long categoryId, Long primaryCategoryId, String categoryName) {
		this.categoryId = categoryId;
		this.primaryCategoryId = primaryCategoryId;
		this.categoryName = categoryName;
	}
}
