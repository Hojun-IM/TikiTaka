package com.trillion.tikitaka.domain.category.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.trillion.tikitaka.global.common.DeleteBaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
	name = "category",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"name", "deleted_at"})
	}
)
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE category SET deleted_at = NOW() WHERE id = ? and version = ?")
public class Category extends DeleteBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Version
	private Long version;

	@Column(nullable = false, length = 25)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "primary_category_id")
	private Category primaryCategory;

	@OneToMany(mappedBy = "primaryCategory", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Category> secondaryCategories = new ArrayList<>();

	@Builder
	public Category(String name, Category primaryCategory) {
		this.name = name;
		this.primaryCategory = primaryCategory;
	}

	public void updateName(String newName) {
		this.name = newName;
	}
}
