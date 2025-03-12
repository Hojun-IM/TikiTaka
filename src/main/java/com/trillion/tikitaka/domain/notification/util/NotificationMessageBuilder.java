package com.trillion.tikitaka.domain.notification.util;

import java.util.List;

import com.trillion.tikitaka.domain.notification.dto.NotificationMessage;
import com.trillion.tikitaka.domain.notification.dto.kakaowork.block.Block;

public interface NotificationMessageBuilder {

	List<Block> buildKakaoWorkMessage(NotificationMessage msg);

	String buildEmailMessage(NotificationMessage msg);
}
