package com.trillion.tikitaka.domain.notification.dto.kakaowork;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.Block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KakaoWorkMessageRequest {

	@JsonProperty("conversation_id")
	private String conversationId;

	private String text;
	private List<Block> blocks;
}
