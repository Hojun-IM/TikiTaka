package com.trillion.tikitaka.domain.ticket.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trillion.tikitaka.domain.ticket.domain.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
