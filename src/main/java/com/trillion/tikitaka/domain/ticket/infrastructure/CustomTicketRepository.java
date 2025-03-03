package com.trillion.tikitaka.domain.ticket.infrastructure;

import org.springframework.data.domain.Page;

import com.trillion.tikitaka.domain.ticket.dto.TicketListResponseForManager;
import com.trillion.tikitaka.domain.ticket.dto.TicketListResponseForUser;
import com.trillion.tikitaka.domain.ticket.dto.TicketResponse;
import com.trillion.tikitaka.domain.ticket.util.TicketFilter;

public interface CustomTicketRepository {
	TicketResponse getTicket(Long ticketId);

	Page<TicketListResponseForManager> getTicketsForManager(TicketFilter filter);

	Page<TicketListResponseForUser> getTicketsForUser(TicketFilter filter, Long requesterId);
}
