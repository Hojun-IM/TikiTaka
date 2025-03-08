package com.trillion.tikitaka.domain.notification.dto.kakaowork.block;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Content {
	private final String type = "text";
	private String text;
	private List<Inline> inlines;
}
