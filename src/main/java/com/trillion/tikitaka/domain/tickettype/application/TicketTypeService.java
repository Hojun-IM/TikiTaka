package com.trillion.tikitaka.domain.tickettype.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trillion.tikitaka.domain.tickettype.dto.TicketTypeListResponse;
import com.trillion.tikitaka.domain.tickettype.dto.TicketTypeRequest;
import com.trillion.tikitaka.domain.tickettype.infrastructure.TicketTypeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketTypeService {

	private final TicketTypeDomainService ticketTypeDomainService;
	private final TicketTypeRepository ticketTypeRepository;

	@Transactional
	public Long createTicketType(TicketTypeRequest request) {
		log.info("[티켓 타입 생성] 타입명: {}", request.getTypeName());
		return ticketTypeDomainService.createTicketType(request.getTypeName());
	}

	public List<TicketTypeListResponse> getTicketTypes() {
		log.info("[티켓 타입 조회]");
		return ticketTypeRepository.getTicketTypes();
	}

	@Transactional
	public Long updateTicketType(Long typeId, TicketTypeRequest request) {
		log.info("[티켓 타입 수정] 타입 ID: {}, 타입명: {}", typeId, request.getTypeName());
		return ticketTypeDomainService.updateTicketType(typeId, request.getTypeName());
	}

	@Transactional
	public void deleteTicketType(Long typeId) {
		log.info("[티켓 타입 삭제] 타입 ID: {}", typeId);
		ticketTypeDomainService.deleteTicketType(typeId);
	}
}
