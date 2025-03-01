package com.trillion.tikitaka.domain.tickettype.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.trillion.tikitaka.global.common.DeleteBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "ticket_type",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"name", "deleted_at"})
	})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE ticket_type SET deleted_at = NOW() WHERE id = ? and version = ?")
public class TicketType extends DeleteBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Version
	private Long version;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private boolean defaultType;

	@Builder
	public TicketType(String name, boolean defaultType) {
		this.name = name;
		this.defaultType = defaultType;
	}

	public void updateName(String name) {
		this.name = name;
	}
}
