package com.trillion.tikitaka.domain.ticket.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TicketRequestForUser {

	@NotEmpty(message = "제목을 입력해주세요")
	@Size(max = 150, message = "제목은 150자를 초과할 수 없습니다.")
	private String title;

	@NotEmpty(message = "상세 내용을 입력해주세요.")
	@Size(max = 5000, message = "상세 내용은 5000자를 초과할 수 없습니다.")
	private String content;

	private Long managerId;

	@NotNull(message = "티켓 유형을 선택해주세요.")
	private Long typeId;

	private Long firstCategoryId;

	private Long secondCategoryId;

	private Boolean urgent;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime deadline;

	public TicketRequestForUser(
		String title, String content, Long managerId, Long typeId, Long firstCategoryId, Long secondCategoryId,
		Boolean urgent, LocalDateTime deadline
	) {
		this.title = title;
		this.content = content;
		this.managerId = managerId;
		this.typeId = typeId;
		this.firstCategoryId = firstCategoryId;
		this.secondCategoryId = secondCategoryId;
		this.urgent = urgent;
		this.deadline = deadline;
	}
}
