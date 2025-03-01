package com.trillion.tikitaka.domain.tickettype.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trillion.tikitaka.domain.tickettype.domain.TicketType;
import com.trillion.tikitaka.domain.tickettype.infrastructure.TicketTypeRepository;
import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketTypeDomainService {

	private final TicketTypeRepository ticketTypeRepository;

	@Transactional
	public Long createTicketType(String typeName) {
		boolean exists = ticketTypeRepository.existsByName(typeName);
		if (exists) {
			throw new BusinessException(ErrorCode.DUPLICATED_TICKET_TYPE_NAME);
		}

		TicketType ticketType = TicketType.builder()
			.name(typeName)
			.defaultType(false)
			.build();

		return ticketTypeRepository.save(ticketType).getId();
	}

	@Transactional
	public Long updateTicketType(Long typeId, String typeName) {
		TicketType ticketType = ticketTypeRepository.findById(typeId).orElseThrow(() -> {
			log.error("[티켓 타입 수정] 타입을 찾을 수 없습니다.");
			return new BusinessException(ErrorCode.TICKET_TYPE_NOT_FOUND);
		});

		if (ticketType.isDefaultType()) {
			throw new BusinessException(ErrorCode.CANNOT_HANDLE_DEFAULT_TICKET_TYPE);
		}

		if (ticketTypeRepository.existsByName(typeName)) {
			throw new BusinessException(ErrorCode.DUPLICATED_TICKET_TYPE_NAME);
		}

		ticketType.updateName(typeName);
		return ticketTypeRepository.save(ticketType).getId();
	}

	@Transactional
	public void deleteTicketType(Long typeId) {
		TicketType ticketType = ticketTypeRepository.findById(typeId).orElseThrow(() -> {
			log.error("[티켓 타입 삭제] 타입을 찾을 수 없습니다.");
			return new BusinessException(ErrorCode.TICKET_TYPE_NOT_FOUND);
		});

		if (ticketType.isDefaultType()) {
			throw new BusinessException(ErrorCode.CANNOT_HANDLE_DEFAULT_TICKET_TYPE);
		}

		ticketTypeRepository.delete(ticketType);
	}
}
