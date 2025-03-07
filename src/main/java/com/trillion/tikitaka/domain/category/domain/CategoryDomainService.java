package com.trillion.tikitaka.domain.category.domain;

import org.springframework.stereotype.Service;

@Service
public class CategoryDomainService {

	public Category createCategory(String categoryName, Category primaryCategory) {
		return Category.builder()
			.name(categoryName)
			.primaryCategory(primaryCategory)
			.build();
	}

	public void updateCategoryName(Category category, String newCategoryName) {
		category.updateName(newCategoryName);
	}
}
