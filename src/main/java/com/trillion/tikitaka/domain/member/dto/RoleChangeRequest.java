package com.trillion.tikitaka.domain.member.dto;

import com.trillion.tikitaka.domain.member.domain.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleChangeRequest {

	private Role role;
}
