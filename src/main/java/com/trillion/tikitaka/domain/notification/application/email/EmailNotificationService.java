package com.trillion.tikitaka.domain.notification.application.email;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.trillion.tikitaka.domain.notification.dto.NotificationMessage;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

	private final JavaMailSender mailSender;

	public boolean sendEmail(NotificationMessage message, String htmlContent) {
		log.info("[이메일 알림 전송 시작] 이메일: {}, 알림 유형: {}, 제목: {}",
			message.getReceiverEmail(),
			message.getType(),
			message.getTitle()
		);

		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

			helper.setSubject(message.getTitle());
			helper.setText(htmlContent, true);
			helper.setTo(message.getReceiverEmail());

			if (message.getSenderEmail() != null) {
				helper.setFrom(message.getSenderEmail());
			}

			mailSender.send(mimeMessage);
			log.info("[이메일 알림 전송 성공] 이메일: {}, 제목: {}", message.getReceiverEmail(), message.getTitle());
			return true;
		} catch (Exception e) {
			log.error("[이메일 알림 전송 실패] 이메일: {}, 에러: {}", message.getReceiverEmail(), e.getMessage(), e);
			return false;
		}
	}
}
