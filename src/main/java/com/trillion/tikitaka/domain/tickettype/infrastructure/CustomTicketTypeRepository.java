package com.trillion.tikitaka.domain.tickettype.infrastructure;

import java.util.List;

import com.trillion.tikitaka.domain.tickettype.dto.TicketTypeListResponse;

public interface CustomTicketTypeRepository {
	List<TicketTypeListResponse> getTicketTypes();
}
