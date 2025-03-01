package com.trillion.tikitaka.domain.category.infrastructure;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.trillion.tikitaka.domain.category.dto.CategoryListResponse;

@Repository
public interface CustomCategoryRepository {
	List<CategoryListResponse> getCategories(Long firstCategoryId);

	// List<WholeCategoryResponse> getWholeCategoryForAdmin();
}
