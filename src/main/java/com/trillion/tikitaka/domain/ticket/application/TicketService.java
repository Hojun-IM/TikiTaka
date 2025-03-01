package com.trillion.tikitaka.domain.ticket.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.trillion.tikitaka.domain.ticket.domain.Ticket;
import com.trillion.tikitaka.domain.ticket.dto.TicketRequestForUser;
import com.trillion.tikitaka.domain.ticket.infrastructure.TicketRepository;
import com.trillion.tikitaka.global.security.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {

	private final TicketDomainService ticketDomainService;
	private final TicketRepository ticketRepository;

	@Transactional
	public Long createTicket(TicketRequestForUser request, List<MultipartFile> files, CustomUserDetails userDetails) {
		log.info("[티켓 생성 요청] 제목: {}", request.getTitle());
		Ticket ticket = ticketDomainService.createTicket(request, userDetails);
		ticketRepository.save(ticket);

		if (files != null && !files.isEmpty()) {
			log.info("[티켓 첨부파일 저장]");
			// TODO: 파일 저장 로직
			// fileService.uploadFiles(ticket.getId(), files);
		}

		return ticket.getId();
	}
}
