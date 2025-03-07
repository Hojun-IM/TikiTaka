package com.trillion.tikitaka.domain.ticket.dto;

import java.time.LocalDateTime;

import org.openapitools.jackson.nullable.JsonNullable;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateRequestForUser {
	// 제목, 내용, 티켓 유형, 1/2차 카테고리, 긴급 여부, 마감기한

	@NotEmpty(message = "제목을 입력해주세요")
	@Size(max = 150, message = "제목은 150자를 초과할 수 없습니다.")
	private String title;

	@NotEmpty(message = "상세 내용을 입력해주세요.")
	@Size(max = 5000, message = "상세 내용은 5000자를 초과할 수 없습니다.")
	private String content;

	private Long typeId;
	private JsonNullable<Long> primaryCategoryId = JsonNullable.undefined();
	private JsonNullable<Long> secondaryCategoryId = JsonNullable.undefined();
	private Boolean urgent;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private JsonNullable<LocalDateTime> deadline = JsonNullable.undefined();
}
