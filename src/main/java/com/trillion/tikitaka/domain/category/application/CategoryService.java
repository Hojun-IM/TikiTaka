package com.trillion.tikitaka.domain.category.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trillion.tikitaka.domain.category.dto.CategoryListResponse;
import com.trillion.tikitaka.domain.category.dto.CategoryRequest;
import com.trillion.tikitaka.domain.category.infrastructure.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

	private final CategoryDomainService categoryDomainService;
	private final CategoryRepository categoryRepository;

	@Transactional
	public Long createCategory(CategoryRequest request) {
		log.info("[카테고리 생성 요창] 카테고리명: {}", request.getName());
		return categoryDomainService.createCategory(request);
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
		return categoryDomainService.updateCategory(categoryId, request);
	}

	@Transactional
	public void deleteCategory(Long categoryId) {
		log.info("[카테고리 삭제 요청] 카테고리 ID: {}", categoryId);
		categoryDomainService.deleteCategory(categoryId);
	}
}
