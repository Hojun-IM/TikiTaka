package com.trillion.tikitaka.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.trillion.tikitaka.domain.member.domain.Role;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberInfoResponse {
	private Long memberId;
	private String username;
	private String email;
	private Role role;
	private String profileImageUrl;

	@QueryProjection
	public MemberInfoResponse(Long memberId, String username, String email, Role role, String profileImageUrl) {
		this.memberId = memberId;
		this.username = username;
		this.email = email;
		this.role = role;
		this.profileImageUrl = (profileImageUrl != null) ? profileImageUrl : "";
	}
}
