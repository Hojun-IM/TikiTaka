package com.trillion.tikitaka.domain.tickettype.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TicketTypeListResponse {
	private Long typeId;
	private String typeName;

	@QueryProjection
	public TicketTypeListResponse(Long typeId, String typeName) {
		this.typeId = typeId;
		this.typeName = typeName;
	}
}
