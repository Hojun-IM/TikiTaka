package com.trillion.tikitaka.domain.notification.dto.kakaowork.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ButtonAction {
	private String type;
	private String name;
	private String value;
}
