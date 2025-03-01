package com.trillion.tikitaka.domain.ticket.presentation;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.trillion.tikitaka.domain.ticket.application.TicketService;
import com.trillion.tikitaka.domain.ticket.dto.TicketRequestForUser;
import com.trillion.tikitaka.global.response.ApiResponse;
import com.trillion.tikitaka.global.security.domain.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TicketController {

	private final TicketService ticketService;

	@PostMapping("/tickets")
	public ApiResponse<Long> createTicket(
		@RequestPart("request") @Valid TicketRequestForUser request,
		@RequestPart(value = "files", required = false) List<@Valid MultipartFile> files,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long ticketId = ticketService.createTicket(request, files, userDetails);
		return ApiResponse.success(ticketId);
	}
}
