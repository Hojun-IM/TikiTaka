package com.trillion.tikitaka.domain.ticket.presentation;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.trillion.tikitaka.domain.ticket.application.TicketService;
import com.trillion.tikitaka.domain.ticket.domain.TicketPriority;
import com.trillion.tikitaka.domain.ticket.domain.TicketStatus;
import com.trillion.tikitaka.domain.ticket.dto.TicketListResponseForManager;
import com.trillion.tikitaka.domain.ticket.dto.TicketListResponseForUser;
import com.trillion.tikitaka.domain.ticket.dto.TicketRequest;
import com.trillion.tikitaka.domain.ticket.dto.TicketResponse;
import com.trillion.tikitaka.domain.ticket.dto.TicketUpdateRequestForManager;
import com.trillion.tikitaka.domain.ticket.dto.TicketUpdateRequestForUser;
import com.trillion.tikitaka.domain.ticket.util.TicketFilter;
import com.trillion.tikitaka.global.common.RestPage;
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
	@GetMapping("/manager/tickets")
	public ApiResponse<RestPage<TicketListResponseForManager>> getTicketsForManager(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "sort", defaultValue = "latest") String sort, // latest, oldest, deadline
		@RequestParam(value = "status", required = false) TicketStatus status,
		@RequestParam(value = "priority", required = false) TicketPriority priority,
		@RequestParam(value = "managerId", required = false) Long managerId,
		@RequestParam(value = "typeId", required = false) Long typeId,
		@RequestParam(value = "primaryCategoryId", required = false) Long primaryCategoryId,
		@RequestParam(value = "secondaryCategoryId", required = false) Long secondaryCategoryId,
		@RequestParam(value = "urgent", required = false) Boolean urgent,
		@RequestParam(value = "keyword", required = false) String keyword
	) {
		TicketFilter filter = new TicketFilter(
			PageRequest.of(page, size), sort, status, priority, managerId, typeId, primaryCategoryId,
			secondaryCategoryId, urgent, keyword
		);
		RestPage<TicketListResponseForManager> response = ticketService.getTicketsForManager(filter);
		return ApiResponse.success(response);
	}

	// 티켓 조회 (사용자)
	@GetMapping("/user/tickets")
	public ApiResponse<RestPage<TicketListResponseForUser>> getTicketsForUser(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "sort", defaultValue = "latest") String sort, // latest, oldest, deadline
		@RequestParam(value = "status", required = false) TicketStatus status,
		@RequestParam(value = "managerId", required = false) Long managerId,
		@RequestParam(value = "typeId", required = false) Long typeId,
		@RequestParam(value = "primaryCategoryId", required = false) Long primaryCategoryId,
		@RequestParam(value = "secondaryCategoryId", required = false) Long secondaryCategoryId,
		@RequestParam(value = "urgent", required = false) Boolean urgent,
		@RequestParam(value = "keyword", required = false) String keyword,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		TicketFilter filter = new TicketFilter(
			PageRequest.of(page, size), sort, status, null, managerId, typeId, primaryCategoryId,
			secondaryCategoryId, urgent, keyword
		);
		RestPage<TicketListResponseForUser> response = ticketService.getTicketsForUser(filter, userDetails);
		return ApiResponse.success(response);
	}

	// 티켓 상세 조회
	@GetMapping("/tickets/{ticketId}")
	public ApiResponse<TicketResponse> getTicket(
		@PathVariable("ticketId") Long ticketId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		TicketResponse response = ticketService.getTicket(ticketId, userDetails);
		return ApiResponse.success(response);
	}

	// 티켓 수정 (매니저)
	@PatchMapping("/manager/tickets/{ticketId}")
	public ApiResponse<Long> updateTicketForManager(
		@PathVariable("ticketId") Long ticketId,
		@RequestBody @Valid TicketUpdateRequestForManager request
	) {
		Long updatedTicketId = ticketService.updateTicketForManager(ticketId, request);
		return ApiResponse.success(updatedTicketId);
	}

	// 티켓 수정 (사용자)
	@PatchMapping("/user/tickets/{ticketId}")
	public ApiResponse<Long> updateTicketForUser(
		@PathVariable("ticketId") Long ticketId,
		@RequestBody @Valid TicketUpdateRequestForUser request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long updatedTicketId = ticketService.updateTicketForUser(ticketId, request, userDetails);
		return ApiResponse.success(updatedTicketId);
	}

	// 티켓 삭제
	@DeleteMapping("/user/tickets/{ticketId}")
	public ApiResponse<Void> deleteTicket(
		@PathVariable("ticketId") Long ticketId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		ticketService.deleteTicket(ticketId, userDetails);
		return ApiResponse.success(null);
	}
}
