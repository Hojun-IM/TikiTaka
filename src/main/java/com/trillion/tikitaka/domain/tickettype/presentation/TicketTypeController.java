package com.trillion.tikitaka.domain.tickettype.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trillion.tikitaka.domain.tickettype.application.TicketTypeService;
import com.trillion.tikitaka.domain.tickettype.dto.TicketTypeListResponse;
import com.trillion.tikitaka.domain.tickettype.dto.TicketTypeRequest;
import com.trillion.tikitaka.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TicketTypeController {

	private final TicketTypeService ticketTypeService;

	@PostMapping("/admin/ticket-types")
	public ApiResponse<Long> createTicketType(
		@RequestBody @Valid TicketTypeRequest request
	) {
		Long typeId = ticketTypeService.createTicketType(request);
		return ApiResponse.success(typeId);
	}

	@GetMapping("/ticket-types")
	public ApiResponse<List<TicketTypeListResponse>> getTicketTypes() {
		List<TicketTypeListResponse> response = ticketTypeService.getTicketTypes();
		return ApiResponse.success(response);
	}

	@PatchMapping("/admin/ticket-types/{typeId}")
	public ApiResponse<Long> updateTicketType(
		@PathVariable("typeId") Long typeId,
		@RequestBody @Valid TicketTypeRequest request
	) {
		Long updatedTypeId = ticketTypeService.updateTicketType(typeId, request);
		return ApiResponse.success(updatedTypeId);
	}

	@DeleteMapping("/admin/ticket-types/{typeId}")
	public ApiResponse<Void> deleteTicketType(
		@PathVariable("typeId") Long typeId
	) {
		ticketTypeService.deleteTicketType(typeId);
		return ApiResponse.success(null);
	}
}
