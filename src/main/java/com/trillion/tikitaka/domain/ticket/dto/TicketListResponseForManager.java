package com.trillion.tikitaka.domain.ticket.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import com.trillion.tikitaka.domain.ticket.domain.TicketPriority;
import com.trillion.tikitaka.domain.ticket.domain.TicketStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TicketListResponseForManager {

	private Long ticketId;
	private String title;
	private String content;
	private TicketStatus status;
	private TicketPriority priority;
	private String typeName;
	private String primaryCategoryName;
	private String secondaryCategoryName;
	private Long managerId;
	private String managerName;
	private Boolean urgent;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDateTime deadline;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@QueryProjection
	public TicketListResponseForManager(
		Long ticketId, String title, String content, TicketPriority priority, TicketStatus status, String typeName,
		String primaryCategoryName, String secondaryCategoryName, Long managerId, String managerName, Boolean urgent,
		LocalDateTime deadline, LocalDateTime createdAt
	) {
		this.ticketId = ticketId;
		this.title = title;
		this.content = content;
		this.priority = priority;
		this.status = status;
		this.typeName = typeName;
		this.primaryCategoryName = primaryCategoryName;
		this.secondaryCategoryName = secondaryCategoryName;
		this.managerId = managerId;
		this.managerName = managerName;
		this.urgent = urgent;
		this.deadline = deadline;
		this.createdAt = createdAt;
	}
}
