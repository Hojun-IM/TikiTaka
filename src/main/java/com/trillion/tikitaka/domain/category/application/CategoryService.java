package com.trillion.tikitaka.domain.category.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trillion.tikitaka.domain.category.domain.Category;
import com.trillion.tikitaka.domain.category.domain.CategoryDomainService;
import com.trillion.tikitaka.domain.category.dto.CategoryListResponse;
import com.trillion.tikitaka.domain.category.dto.CategoryRequest;
import com.trillion.tikitaka.domain.category.infrastructure.CategoryRepository;
import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryDomainService categoryDomainService;
	private final CategoryRepository categoryRepository;

	@Transactional
	public Long createCategory(CategoryRequest request) {
		log.info("[카테고리 생성 요창] 카테고리명: {}", request.getName());

		if (categoryRepository.existsByName(request.getName())) {
			log.error("[카테고리 생성 실패] 중복된 카테고리명: {}", request.getName());
			throw new BusinessException(ErrorCode.DUPLICATED_CATEGORY_NAME);
		}

		Category primaryCategory = null;
		if (request.getPrimaryCategoryId() != null) {
			primaryCategory = categoryRepository.findByIdAndPrimaryCategoryIsNull(request.getPrimaryCategoryId())
				.orElseThrow(() -> {
					log.error("[카테고리 생성 실패] 1차 카테고리를 찾을 수 없음");
					return new BusinessException(ErrorCode.PRIMARY_CATEGORY_NOT_FOUND);
				});
		}

		Category category = categoryDomainService.createCategory(request.getName(), primaryCategory);
		return categoryRepository.save(category).getId();
	}

	// public List<WholeCategoryResponse> getWholeCategories() {
	// 	log.info("[전체 카테고리 조회 요청]");
	// 	return categoryRepository.getWholeCategoryForAdmin();
	// }

	public List<CategoryListResponse> getCategories(Long primaryCategoryId) {
		log.info("[카테고리 조회 요청]");
		return categoryRepository.getCategories(primaryCategoryId);
	}

	@Transactional
	public Long updateCategory(Long categoryId, CategoryRequest request) {
		log.info("[카테고리 수정 요청] 카테고리 ID: {}", categoryId);

		Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
			log.error("[카테고리 수정 실패] 카테고리를 찾을 수 없음");
			return new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
		});

		if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
			throw new BusinessException(ErrorCode.DUPLICATED_CATEGORY_NAME);
		}

		categoryDomainService.updateCategoryName(category, request.getName());
		return category.getId();
	}

	@Transactional
	public void deleteCategory(Long categoryId) {
		log.info("[카테고리 삭제 요청] 카테고리 ID: {}", categoryId);

		Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
			log.error("[카테고리 삭제 실패] 카테고리를 찾을 수 없음");
			return new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
		});

		categoryRepository.delete(category);
	}
}
