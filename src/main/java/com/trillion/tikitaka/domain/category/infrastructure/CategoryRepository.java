package com.trillion.tikitaka.domain.category.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trillion.tikitaka.domain.category.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>, CustomCategoryRepository {
	boolean existsByName(String name);

	Optional<Category> findByIdAndPrimaryCategoryIsNull(Long id);
}
