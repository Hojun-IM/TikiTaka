package com.trillion.tikitaka.domain.category.domain;

import org.springframework.stereotype.Service;

import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	public void validateHierarchy(Category primaryCategory, Category secondaryCategory) {
		if (secondaryCategory != null) {
			if (primaryCategory == null) {
				log.error("[카테고리 유효성 검사 실패] 1차 카테고리가 존재하지 않는 상태에서 2차 카테고리가 존재");
				throw new BusinessException(ErrorCode.CATEGORY_MISMATCH);
			}

			if (secondaryCategory.getPrimaryCategory() == null
				|| !secondaryCategory.getPrimaryCategory().getId().equals(primaryCategory.getId())) {
				log.error("[카테고리 유효성 검사 실패] 1차 카테고리와 2차 카테고리의 계층 구조 불일치");
				throw new BusinessException(ErrorCode.CATEGORY_MISMATCH);
			}
		}
	}
}
