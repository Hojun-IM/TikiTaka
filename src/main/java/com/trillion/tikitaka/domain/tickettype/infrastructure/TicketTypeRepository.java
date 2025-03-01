package com.trillion.tikitaka.domain.tickettype.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trillion.tikitaka.domain.tickettype.domain.TicketType;

public interface TicketTypeRepository extends JpaRepository<TicketType, Long>, CustomTicketTypeRepository {
	boolean existsByName(String name);
}
