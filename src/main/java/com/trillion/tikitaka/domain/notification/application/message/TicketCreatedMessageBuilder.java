package com.trillion.tikitaka.domain.notification.application.message;

import static com.trillion.tikitaka.domain.notification.dto.kakaowork.block.ButtonBlock.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.trillion.tikitaka.domain.notification.domain.NotificationType;
import com.trillion.tikitaka.domain.notification.dto.NotificationMessage;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.Block;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.ButtonAction;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.ButtonBlock;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.Content;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.DescriptionBlock;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.HeaderBlock;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.Inline;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.TextBlock;
import com.trillion.tikitaka.domain.notification.util.NotificationMessageBuilder;
import com.trillion.tikitaka.domain.notification.util.NotificationTypeQualifier;

@Component
@NotificationTypeQualifier(NotificationType.TICKET_CREATED)
public class TicketCreatedMessageBuilder implements NotificationMessageBuilder {

	private String safeString(Object obj) {
		return (obj == null) ? "-" : obj.toString();
	}

	@Override
	public List<Block> buildKakaoWorkMessage(NotificationMessage msg) {
		List<Block> blocks = new ArrayList<>();
		Map<String, Object> details = msg.getDetails();

		// 헤더
		HeaderBlock header = new HeaderBlock("티켓 생성 알림", "blue");
		blocks.add(header);

		// 본문
		String textValue = String.format("[#%s] %s", details.get("ticketId"), details.get("title"));
		List<Inline> inlineTexts = List.of(new Inline("styled", textValue, true, "blue"));
		TextBlock textBlock = new TextBlock(textValue, inlineTexts);
		blocks.add(textBlock);

		// 티켓 상세 내용 (유형, 카테고리, 담당자, 요청자)
		String typeText = details.get("type").toString();
		List<Inline> inlineType = List.of(new Inline("styled", typeText, true));
		DescriptionBlock typeBlock = new DescriptionBlock(
			new Content(typeText, inlineType), "유형", true
		);
		blocks.add(typeBlock);

		String primaryCategoryText = safeString(details.get("primaryCategory"));
		String secondaryCategoryText = safeString(details.get("secondaryCategory"));
		String categoryText = primaryCategoryText + "/" + secondaryCategoryText;
		List<Inline> inlineCategory = List.of(new Inline("styled", categoryText, true));
		DescriptionBlock categoryBlock = new DescriptionBlock(
			new Content(categoryText, inlineCategory), "카테고리", true
		);
		blocks.add(categoryBlock);

		String managerText = safeString(details.get("manager").toString());
		List<Inline> inlineManager = List.of(new Inline("styled", managerText, true));
		DescriptionBlock managerBlock = new DescriptionBlock(
			new Content(managerText, inlineManager), "담당자", true
		);
		blocks.add(managerBlock);

		String requesterText = details.get("requester").toString();
		List<Inline> inlineRequester = List.of(new Inline("styled", requesterText, true));
		DescriptionBlock requesterBlock = new DescriptionBlock(
			new Content(requesterText, inlineRequester), "요청자", true
		);
		blocks.add(requesterBlock);

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
