package com.trillion.tikitaka.domain.notification.application.message;

import java.util.ArrayList;
import java.util.List;

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
@NotificationTypeQualifier(NotificationType.REGISTRATION_REJECTED)
public class RegistrationRejectedMessageBuilder implements NotificationMessageBuilder {

	@Override
	public List<Block> buildKakaoWorkMessage(NotificationMessage msg) {
		List<Block> blocks = new ArrayList<>();

		// 헤더
		HeaderBlock header = new HeaderBlock("티키타카 계정 등록 실패", "white");
		blocks.add(header);

		// 본문
		TextBlock textBlock = new TextBlock("티키타카 계정 등록이 거절되었습니다. 자세한 내용은 담당자에게 문의해주세요.");
		Inline inline = new Inline("styled", String.valueOf(msg.getDetails().get("reason")), true);
		Content content = new Content(String.valueOf(msg.getDetails().get("reason")), List.of(inline));
		blocks.add(textBlock);

		// 설명
		DescriptionBlock descriptionBlock = new DescriptionBlock(content, "사유", true);
		blocks.add(descriptionBlock);

		// 버튼
		ButtonAction buttonAction = new ButtonAction("open_system_browser", "티키타카 바로가기", "https://www.tikitaka.com");
		ButtonBlock buttonBlock = new ButtonBlock("티키타카 바로가기", "default", buttonAction);
		blocks.add(buttonBlock);

		return blocks;
	}

	@Override
	public String buildEmailMessage(NotificationMessage msg) {
		// 계정 등록 처리는 카카오워크 메시지만 전송
		return null;
	}
}
