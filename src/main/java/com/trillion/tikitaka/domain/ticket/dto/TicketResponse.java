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
public class TicketResponse {

	private Long ticketId;
	private String title;
	private String content;
	private TicketStatus status;
	private TicketPriority priority;
	private Long typeId;
	private String typeName;
	private Long primaryCategoryId;
	private String primaryCategoryName;
	private Long secondaryCategoryId;
	private String secondaryCategoryName;
	private Long managerId;
	private String managerName;
	private Long requesterId;
	private String requesterName;
	private Boolean urgent;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDateTime deadline;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	@QueryProjection
	public TicketResponse(
		Long ticketId, String title, String content, TicketPriority priority, TicketStatus status,
		Long typeId, String typeName, Long primaryCategoryId, String primaryCategoryName, Long secondaryCategoryId,
		String secondaryCategoryName, Long managerId, String managerName, Long requesterId, String requesterName,
		Boolean urgent, LocalDateTime deadline, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.ticketId = ticketId;
		this.title = title;
		this.content = content;
		this.priority = priority;
		this.status = status;
		this.typeId = typeId;
		this.typeName = typeName;
		this.primaryCategoryId = primaryCategoryId;
		this.primaryCategoryName = primaryCategoryName;
		this.secondaryCategoryId = secondaryCategoryId;
		this.secondaryCategoryName = secondaryCategoryName;
		this.managerId = managerId;
		this.managerName = managerName;
		this.requesterId = requesterId;
		this.requesterName = requesterName;
		this.urgent = urgent;
		this.deadline = deadline;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
