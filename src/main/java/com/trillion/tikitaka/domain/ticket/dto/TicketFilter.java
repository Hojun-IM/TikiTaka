package com.trillion.tikitaka.domain.ticket.dto;

import org.springframework.data.domain.Pageable;

import com.trillion.tikitaka.domain.ticket.domain.TicketPriority;
import com.trillion.tikitaka.domain.ticket.domain.TicketStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TicketFilter {

	private Pageable pageable;
	private String sort = "latest";
	private TicketStatus status;
	private TicketPriority priority;
	private Long managerId;
	private Long typeId;
	private Long primaryCategoryId;
	private Long secondaryCategoryId;
	private Boolean urgent;
	private String keyword;

	public TicketFilter(
		Pageable pageable, String sort, TicketStatus status, TicketPriority priority, Long managerId,
		Long typeId, Long primaryCategoryId, Long secondaryCategoryId, Boolean urgent, String keyword
	) {
		this.pageable = pageable;
		this.sort = sort;
		this.status = status;
		this.priority = priority;
		this.managerId = managerId;
		this.typeId = typeId;
		this.primaryCategoryId = primaryCategoryId;
		this.secondaryCategoryId = secondaryCategoryId;
		this.urgent = urgent;
		this.keyword = keyword;
	}
}
