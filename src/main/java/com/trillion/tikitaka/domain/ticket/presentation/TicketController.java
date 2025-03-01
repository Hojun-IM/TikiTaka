package com.trillion.tikitaka.domain.ticket.presentation;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.trillion.tikitaka.domain.ticket.application.TicketService;
import com.trillion.tikitaka.domain.ticket.dto.TicketRequest;
import com.trillion.tikitaka.domain.ticket.dto.TicketUpdateRequestForManager;
import com.trillion.tikitaka.domain.ticket.dto.TicketUpdateRequestForUser;
import com.trillion.tikitaka.global.response.ApiResponse;
import com.trillion.tikitaka.global.security.domain.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TicketController {

	private final TicketService ticketService;

	@PostMapping(value = "/tickets", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<Long> createTicket(
		@RequestPart("request") @Valid TicketRequest request,
		@RequestPart(value = "files", required = false) List<@Valid MultipartFile> files,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long ticketId = ticketService.createTicket(request, files, userDetails);
		return ApiResponse.success(ticketId);
	}

	// 티켓 조회 (매니저)

	// 티켓 조회 (사용자)

	// 티켓 상세 조회

	// 티켓 수정 (매니저)
	@PatchMapping("/manager/tickets/{ticketId}")
	public ApiResponse<Void> updateTicketForManager(
		@PathVariable("ticketId") Long ticketId,
		@RequestBody @Valid TicketUpdateRequestForManager request
	) {
		ticketService.updateTicketForManager(ticketId, request);
		return ApiResponse.success(null);
	}

	// 티켓 수정 (사용자)
	@PatchMapping("/user/tickets/{ticketId}")
	public ApiResponse<Void> updateTicketForUser(
		@PathVariable("ticketId") Long ticketId,
		@RequestBody @Valid TicketUpdateRequestForUser request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		ticketService.updateTicketForUser(ticketId, request, userDetails);
		return ApiResponse.success(null);
	}

	// 티켓 삭제
}
