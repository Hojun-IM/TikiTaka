package com.trillion.tikitaka.domain.member.dto;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberInfoListResponse {
	private Page<MemberInfoResponse> memberInfo;
	Long adminCount;
	Long managerCount;
	Long userCount;

	public void setRoleCount(Long adminCount, Long managerCount, Long userCount) {
		this.adminCount = adminCount;
		this.managerCount = managerCount;
		this.userCount = userCount;
	}
}
