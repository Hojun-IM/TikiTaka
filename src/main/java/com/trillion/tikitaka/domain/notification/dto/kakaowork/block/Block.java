package com.trillion.tikitaka.domain.notification.dto.kakaowork.block;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	property = "type",
	visible = true
)
@JsonSubTypes({
	@JsonSubTypes.Type(value = HeaderBlock.class, name = "header"),
	@JsonSubTypes.Type(value = TextBlock.class, name = "text"),
	@JsonSubTypes.Type(value = DescriptionBlock.class, name = "description"),
	@JsonSubTypes.Type(value = DividerBlock.class, name = "divider"),
	@JsonSubTypes.Type(value = ButtonBlock.class, name = "button")
})
public interface Block {
}
