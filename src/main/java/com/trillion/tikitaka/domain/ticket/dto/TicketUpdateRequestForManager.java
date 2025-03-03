package com.trillion.tikitaka.domain.ticket.dto;

import org.openapitools.jackson.nullable.JsonNullable;

import com.trillion.tikitaka.domain.ticket.domain.TicketPriority;
import com.trillion.tikitaka.domain.ticket.domain.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateRequestForManager {
	// 담당자, 상태, 우선순위, 티켓 유형, 1/2차 카테고리

	private JsonNullable<Long> managerId = JsonNullable.undefined();
	private TicketStatus status;
	private JsonNullable<TicketPriority> priority = JsonNullable.undefined();
	private Long typeId;
	private JsonNullable<Long> primaryCategoryId = JsonNullable.undefined();
	private JsonNullable<Long> secondaryCategoryId = JsonNullable.undefined();
}
