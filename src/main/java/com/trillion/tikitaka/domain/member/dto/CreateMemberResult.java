package com.trillion.tikitaka.domain.member.dto;

import com.trillion.tikitaka.domain.member.domain.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMemberResult {
	private Member newMember;
	private String createdPassword;
}
