package com.trillion.tikitaka.domain.ticket.infrastructure;

import org.springframework.data.domain.Page;

import com.trillion.tikitaka.domain.ticket.dto.TicketFilter;
import com.trillion.tikitaka.domain.ticket.dto.TicketListResponse;
import com.trillion.tikitaka.domain.ticket.dto.TicketResponse;

public interface CustomTicketRepository {
	TicketResponse getTicket(Long ticketId);

	Page<TicketListResponse> getTicketsForManager(TicketFilter filter);
}
