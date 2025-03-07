package com.trillion.tikitaka.domain.registration.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.trillion.tikitaka.global.common.DeleteBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Table(name = "registration")
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = "reason")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE registration SET deleted_at = NOW(), version = version + 1 WHERE id = ? AND version = ?")
public class Registration extends DeleteBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Version
	@Column(nullable = false)
	private Long version;

	@NotBlank
	@Size(max = 30)
	@Pattern(regexp = "^[a-z]{3,10}\\.[a-z]{1,5}$")
	@Column(nullable = false)
	private String username;

	@NotBlank
	@Email
	@Column(nullable = false)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RegistrationStatus status;

	@Size(max = 300)
	private String reason;

	@Builder
	public Registration(String username, String email, RegistrationStatus status) {
		this.username = username;
		this.email = email;
		this.status = status;
	}

	public void approve(String approveReason) {
		this.status = RegistrationStatus.APPROVED;
		this.reason = approveReason != null ? approveReason : "계정 등록이 승인되었습니다.";
	}

	public void reject(String rejectReason) {
		this.status = RegistrationStatus.REJECTED;
		this.reason = rejectReason != null ? rejectReason : "계정 등록이 거절되었습니다.";
	}
}
