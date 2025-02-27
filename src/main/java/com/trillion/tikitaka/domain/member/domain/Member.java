package com.trillion.tikitaka.domain.member.domain;

import java.time.LocalDateTime;

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
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Table(name = "member")
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = "password")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE id = ? AND version = ?")
public class Member extends DeleteBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Version
	@Column(nullable = false)
	private Long version;

	@NotBlank
	@Size(max = 20)
	@Column(nullable = false, unique = true)
	private String username;

	@NotBlank
	@Size(min = 8)
	@Column(nullable = false)
	private String password;

	@Email
	@NotBlank
	@Column(nullable = false, unique = true)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	private String profileImageUrl;

	@Column(nullable = false)
	private boolean accountNonLocked = true;

	@Column(nullable = false)
	private int loginFailureCount = 0;

	private LocalDateTime lockReleaseTime;

	private LocalDateTime lastLoginAt;

	private LocalDateTime lastPasswordChangedAt;

	@Builder
	public Member(
		String username, String password, String email, Role role, String profileImageUrl,
		Boolean accountNonLocked, Integer loginFailureCount, LocalDateTime lockReleaseTime,
		LocalDateTime lastLoginAt, LocalDateTime lastPasswordChangedAt
	) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
		this.profileImageUrl =
			profileImageUrl != null ? profileImageUrl : "https://tikitaka.kr/images/default-profile.png";
		this.accountNonLocked = accountNonLocked != null ? accountNonLocked : true;
		this.loginFailureCount = loginFailureCount != null ? loginFailureCount : 0;
		this.lockReleaseTime = lockReleaseTime;
		this.lastLoginAt = lastLoginAt;
		this.lastPasswordChangedAt = lastPasswordChangedAt;
	}

	// CustomUserDetails 객체 생성 시 사용
	public Member(Long memberId, String username, String role) {
		this.id = memberId;
		this.username = username;
		this.role = Role.valueOf(role);
	}

	public void increaseLoginFailureCount() {
		this.loginFailureCount++;
	}

	public void resetLoginFailCount() {
		this.loginFailureCount = 0;
	}

	public void lockAccount(LocalDateTime lockExpireAt) {
		this.accountNonLocked = false;
		this.lockReleaseTime = lockExpireAt;
	}

	public void unlockAccount() {
		this.accountNonLocked = true;
		this.lockReleaseTime = null;
	}

	public void updateLastLoginAt(LocalDateTime loginAt) {
		this.lastLoginAt = loginAt;
	}

	public void updatePassword(String newPassword) {
		this.password = newPassword;
		this.lastPasswordChangedAt = LocalDateTime.now();
	}
}
