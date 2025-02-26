package com.trillion.tikitaka.domain.member.presentation;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trillion.tikitaka.domain.member.application.MemberService;
import com.trillion.tikitaka.domain.member.domain.Role;
import com.trillion.tikitaka.domain.member.dto.MemberInfoListResponse;
import com.trillion.tikitaka.domain.member.dto.MemberInfoResponse;
import com.trillion.tikitaka.global.response.ApiResponse;
import com.trillion.tikitaka.global.security.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	// 내 정보 조회
	@GetMapping("/member/me")
	public ApiResponse<MemberInfoResponse> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
		MemberInfoResponse response = memberService.getMyInfo(userDetails);
		return ApiResponse.success("내 정보 조회에 성공했습니다.", response);
	}

	// 역할별 필터링을 적용한 전체 사용자 조회 (관리자용)
	@GetMapping("/admin/members")
	public ApiResponse<MemberInfoListResponse> getAllMembersForAdmin(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "20") int size,
		@RequestParam(value = "role", required = false) Role role
	) {
		Pageable pageable = PageRequest.of(page, size);
		MemberInfoListResponse allMembersForAdmin = memberService.getAllMembersForAdmin(pageable, role);
		return ApiResponse.success(allMembersForAdmin);
	}

	// 역할별 필터링을 적용한 전체 사용자 조회 (매니저, 사용자용)

	// 특정 사용자 조회

	// 특정 사용자 조회

	// 내 비밀번호 변경

	// 사용자 삭제 처리 (관리자용)

	// 사용자 역할 변경 (관리자용)
}
