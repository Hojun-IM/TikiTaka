package com.trillion.tikitaka.domain.notification.application.message;

import static com.trillion.tikitaka.domain.notification.dto.kakaowork.block.ButtonBlock.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trillion.tikitaka.domain.notification.domain.NotificationType;
import com.trillion.tikitaka.domain.notification.dto.NotificationMessage;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.Block;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.ButtonAction;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.ButtonBlock;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.Content;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.DescriptionBlock;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.DividerBlock;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.HeaderBlock;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.Inline;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.TextBlock;
import com.trillion.tikitaka.domain.notification.util.NotificationMessageBuilder;
import com.trillion.tikitaka.domain.notification.util.NotificationTypeQualifier;
import com.trillion.tikitaka.domain.ticket.dto.TicketUpdatedValue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@NotificationTypeQualifier(NotificationType.TICKET_UPDATED)
public class TicketUpdatedMessageBuilder implements NotificationMessageBuilder {

	private final ObjectMapper objectMapper;

	public TicketUpdatedMessageBuilder(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	private String safeString(Object obj) {
		return (obj == null) ? "-" : obj.toString();
	}

	@Override
	public List<Block> buildKakaoWorkMessage(NotificationMessage msg) {
		List<Block> blocks = new ArrayList<>();
		Map<String, Object> details = msg.getDetails();

		// 헤더
		HeaderBlock header = new HeaderBlock("티켓 수정 알림", "yellow");
		blocks.add(header);

		// 본문
		String textValue = String.format("[#%s] %s", details.get("ticketId"), details.get("title"));
		List<Inline> inlineTexts = List.of(new Inline("styled", textValue, true, "default"));
		TextBlock textBlock = new TextBlock(textValue, inlineTexts);
		blocks.add(textBlock);

		// 티켓 변경 내용 (유형, 카테고리, 담당자, 요청자)
		Object changes = details.get("changes");

		if (changes instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> rawList = (List<Object>)changes;

			try {
				String json = objectMapper.writeValueAsString(rawList);
				List<TicketUpdatedValue> changedList = objectMapper.readValue(
					json, new TypeReference<List<TicketUpdatedValue>>() {
					}
				);

				for (TicketUpdatedValue dto : changedList) {
					String fieldName = dto.getFieldName();
					String oldVal = safeString(dto.getOldValue());
					String newVal = safeString(dto.getNewValue());

					blocks.add(new DividerBlock());

					List<Inline> inlineFieldTitle = List.of(new Inline("styled", fieldName, true, "default"));
					blocks.add(new TextBlock(fieldName, inlineFieldTitle));

					List<Inline> inlineOldValue = List.of(new Inline("styled", oldVal, true));
					blocks.add(new DescriptionBlock(new Content(oldVal, inlineOldValue), "변경 전", true));

					List<Inline> inlineNewValue = List.of(new Inline("styled", newVal, true));
					blocks.add(new DescriptionBlock(new Content(newVal, inlineNewValue), "변경 후", true));
				}
			} catch (Exception e) {
				log.error("[티켓 수정 알림] 메시지 생성 중 오류 발생: {}", e.getMessage());
			}
		}

		// 버튼
		String url = END_POINT + "/manager/detail/" + details.get("ticketId");
		ButtonAction buttonAction = new ButtonAction("open_system_browser", "티키타카 바로가기", url);
		ButtonBlock buttonBlock = new ButtonBlock("티키타카 바로가기", "default", buttonAction);
		blocks.add(buttonBlock);

		return blocks;
	}

	@Override
	public String buildEmailMessage(NotificationMessage msg) {
		return null;
	}
}
