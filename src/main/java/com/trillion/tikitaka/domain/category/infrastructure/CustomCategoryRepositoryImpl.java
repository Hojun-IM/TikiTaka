package com.trillion.tikitaka.domain.category.infrastructure;

import static com.trillion.tikitaka.domain.category.domain.QCategory.*;
import static com.trillion.tikitaka.domain.tickettype.domain.QTicketType.*;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trillion.tikitaka.domain.category.dto.CategoryListResponse;
import com.trillion.tikitaka.domain.category.dto.QCategoryListResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCategoryRepositoryImpl implements CustomCategoryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<CategoryListResponse> getCategories(Long firstCategoryId) {

		return queryFactory
			.select(new QCategoryListResponse(
				category.id.as("categoryId"),
				category.primaryCategory.id.as("primaryCategoryId"),
				category.name.as("categoryName")
			))
			.from(category)
			.where(
				firstCategoryCond(firstCategoryId),
				deletedAtIsNull()
			)
			.fetch();
	}

	// @Override
	// public List<WholeCategoryResponse> getWholeCategoryForAdmin() {
	// 	QCategory primary = category;
	// 	QCategory secondary = new QCategory("secondaryCategory");
	//
	// 	return queryFactory
	// 		.select(new QWholeCategoryResponse(
	// 			primary.id,
	// 			primary.name,
	// 			JPAExpressions.select(GroupBy.list(
	// 					Projections.constructor(SubCategoryResponse.class,
	// 						secondary.id,
	// 						secondary.name
	// 					)
	// 				))
	// 				.from(secondary)
	// 				.where(secondary.primaryCategory.id.eq(primary.id))
	// 		))
	// 		.from(primary)
	// 		.where(primary.primaryCategory.isNull())
	// 		.fetch();
	// }

	private BooleanExpression deletedAtIsNull() {
		return ticketType.deletedAt.isNull();
	}

	private BooleanExpression firstCategoryCond(Long firstCategoryId) {
		return firstCategoryId != null
			? category.primaryCategory.id.eq(firstCategoryId)
			: category.primaryCategory.id.isNull();
	}
}
