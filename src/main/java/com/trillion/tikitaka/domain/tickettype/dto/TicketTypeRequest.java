package com.trillion.tikitaka.domain.tickettype.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketTypeRequest {

	@NotEmpty
	@Length(min = 1, max = 15)
	private String typeName;
}
