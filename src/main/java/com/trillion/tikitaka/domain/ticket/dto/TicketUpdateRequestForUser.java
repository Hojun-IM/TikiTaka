package com.trillion.tikitaka.domain.ticket.dto;

import java.time.LocalDateTime;

import org.openapitools.jackson.nullable.JsonNullable;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateRequestForUser {
	// 제목, 내용, 티켓 유형, 1/2차 카테고리, 긴급 여부, 마감기한

	private String title;
	private String content;
	private Long typeId;
	private JsonNullable<Long> primaryCategoryId = JsonNullable.undefined();
	private JsonNullable<Long> secondaryCategoryId = JsonNullable.undefined();
	private Boolean urgent;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private JsonNullable<LocalDateTime> deadline = JsonNullable.undefined();
}
