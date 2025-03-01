package com.trillion.tikitaka.domain.ticket.infrastructure;

import com.trillion.tikitaka.domain.ticket.dto.TicketResponse;

public interface CustomTicketRepository {
	TicketResponse getTicket(Long ticketId);
}
