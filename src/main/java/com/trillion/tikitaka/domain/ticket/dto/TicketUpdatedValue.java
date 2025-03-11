package com.trillion.tikitaka.domain.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdatedValue {
	private String fieldName;
	private Object oldValue;
	private Object newValue;
}
