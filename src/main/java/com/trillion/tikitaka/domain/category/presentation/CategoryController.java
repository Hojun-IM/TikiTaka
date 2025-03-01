package com.trillion.tikitaka.domain.category.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trillion.tikitaka.domain.category.application.CategoryService;
import com.trillion.tikitaka.domain.category.dto.CategoryListResponse;
import com.trillion.tikitaka.domain.category.dto.CategoryRequest;
import com.trillion.tikitaka.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping("/admin/categories")
	public ApiResponse<Long> createCategory(
		@RequestBody @Valid CategoryRequest request
	) {
		Long categoryId = categoryService.createCategory(request);
		return ApiResponse.success(categoryId);
	}

	// @GetMapping("/admin/categories")
	// public ApiResponse<List<WholeCategoryResponse>> getWholeCategories() {
	// 	List<WholeCategoryResponse> response = categoryService.getWholeCategories();
	// 	return ApiResponse.success(response);
	// }

	@GetMapping("/categories")
	public ApiResponse<List<CategoryListResponse>> getCategories(
		@RequestParam(value = "primaryCategoryId", required = false) Long primaryCategoryId
	) {
		List<CategoryListResponse> response = categoryService.getCategories(primaryCategoryId);
		return ApiResponse.success(response);
	}

	@PatchMapping("/admin/categories/{categoryId}")
	public ApiResponse<Long> updateCategory(
		@PathVariable("categoryId") Long categoryId,
		@RequestBody @Valid CategoryRequest request
	) {
		Long updatedCategoryId = categoryService.updateCategory(categoryId, request);
		return ApiResponse.success(updatedCategoryId);
	}

	@DeleteMapping("/admin/categories/{categoryId}")
	public ApiResponse<Void> deleteCategory(
		@PathVariable("categoryId") Long categoryId
	) {
		categoryService.deleteCategory(categoryId);
		return ApiResponse.success(null);
	}
}
