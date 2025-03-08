package com.trillion.tikitaka.domain.ticket.domain;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.trillion.tikitaka.domain.category.domain.Category;
import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.domain.tickettype.domain.TicketType;

@Service
public class TicketDomainService {

	public Ticket createTicket(
		String title, String content, Member manager, Member requester, TicketType ticketType, Category primaryCategory,
		Category secondaryCategory, Boolean urgent, LocalDateTime deadline
	) {
		return Ticket.builder()
			.title(title)
			.content(content)
			.status(TicketStatus.PENDING)
			.priority(null)
			.manager(manager)
			.requester(requester)
			.ticketType(ticketType)
			.primaryCategory(primaryCategory)
			.secondaryCategory(secondaryCategory)
			.urgent(urgent != null ? urgent : false)
			.deadline(deadline)
			.build();
	}

	public void updateTicketForManager(
		Ticket ticket, TicketStatus newStatus, TicketPriority newPriority, Member newManager, TicketType newTicketType,
		Category newPrimaryCategory, Category newSecondaryCategory
	) {
		if (newStatus != null) {
			ticket.updateStatus(newStatus);
		}

		ticket.updatePriority(newPriority);

		ticket.updateManager(newManager);

		if (newTicketType != null) {
			ticket.updateTicketType(newTicketType);
		}

		ticket.updatePrimaryCategory(newPrimaryCategory);
		ticket.updateSecondaryCategory(newSecondaryCategory);
	}

	public void updateTicketForUser(
		Ticket ticket, String newTitle, String newContent, TicketType newTicketType, Category newPrimaryCategory,
		Category newSecondaryCategory, Boolean newUrgent, LocalDateTime newDeadline
	) {
		if (newTitle != null) {
			ticket.updateTitle(newTitle);
		}

		if (newContent != null) {
			ticket.updateContent(newContent);
		}

		if (newTicketType != null) {
			ticket.updateTicketType(newTicketType);
		}

		ticket.updatePrimaryCategory(newPrimaryCategory);
		ticket.updateSecondaryCategory(newSecondaryCategory);

		if (newUrgent != null) {
			ticket.updateUrgent(newUrgent);
		}

		if (newDeadline != null) {
			ticket.updateDeadline(newDeadline);
		}
	}
}
