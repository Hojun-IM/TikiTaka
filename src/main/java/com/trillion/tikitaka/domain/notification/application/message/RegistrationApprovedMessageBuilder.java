package com.trillion.tikitaka.domain.notification.application.message;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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

import lombok.RequiredArgsConstructor;

@Component
@NotificationTypeQualifier(NotificationType.REGISTRATION_APPROVED)
@RequiredArgsConstructor
public class RegistrationApprovedMessageBuilder implements NotificationMessageBuilder {

	private final TemplateEngine templateEngine;

	@Override
	public List<Block> buildKakaoWorkMessage(NotificationMessage msg) {
		List<Block> blocks = new ArrayList<>();

		// 헤더
		HeaderBlock header = new HeaderBlock("티키타카 계정 등록 완료", "white");
		blocks.add(header);

		// 본문
		TextBlock textBlock = new TextBlock("티키타카 계정 등록이 승인되었습니다. 등록한 계정 임시 비밀번호가 발급되었습니다.");
		Inline inline = new Inline("styled", String.valueOf(msg.getDetails().get("password")), true);
		Content content = new Content(String.valueOf(msg.getDetails().get("password")), List.of(inline));
		blocks.add(textBlock);

		// 설명
		DescriptionBlock descriptionBlock = new DescriptionBlock(content, "비밀번호", true);
		blocks.add(descriptionBlock);

		// 버튼
		ButtonAction buttonAction = new ButtonAction("open_system_browser", "티키타카 바로가기", "https://www.tikitaka.com");
		ButtonBlock buttonBlock = new ButtonBlock("티키타카 바로가기", "default", buttonAction);
		blocks.add(buttonBlock);

		return blocks;
	}

	@Override
	public String buildEmailMessage(NotificationMessage msg) {
		// TODO: 템플릿 양식 수정 필요
		Context context = new Context();
		context.setVariable("username", msg.getReceiverEmail());
		context.setVariable("password", msg.getDetails().get("password"));

		return templateEngine.process("email/registrationApproved", context);
	}
}
