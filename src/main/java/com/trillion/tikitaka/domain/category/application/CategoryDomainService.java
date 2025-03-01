package com.trillion.tikitaka.domain.category.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trillion.tikitaka.domain.category.domain.Category;
import com.trillion.tikitaka.domain.category.dto.CategoryRequest;
import com.trillion.tikitaka.domain.category.infrastructure.CategoryRepository;
import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryDomainService {

	private final CategoryRepository categoryRepository;

	@Transactional
	public Long createCategory(CategoryRequest request) {
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

		Category category = Category.builder()
			.name(request.getName())
			.primaryCategory(primaryCategory)
			.build();

		return categoryRepository.save(category).getId();
	}

	@Transactional
	public Long updateCategory(Long categoryId, CategoryRequest request) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> {
				log.error("[카테고리 수정 실패] 카테고리를 찾을 수 없음");
				return new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
			});

		// 이름 중복 검사 (다른 카테고리에서 동일 이름이 사용 중인 경우)
		if (!category.getName().equals(request.getName())
			&& categoryRepository.existsByName(request.getName())) {
			throw new BusinessException(ErrorCode.DUPLICATED_CATEGORY_NAME);
		}

		category.updateName(request.getName());
		return categoryRepository.save(category).getId();
	}

	@Transactional
	public void deleteCategory(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> {
				log.error("[카테고리 삭제 실패] 카테고리를 찾을 수 없음");
				return new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
			});

		categoryRepository.delete(category);
	}
}
