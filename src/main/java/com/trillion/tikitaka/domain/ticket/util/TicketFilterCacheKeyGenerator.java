package com.trillion.tikitaka.domain.ticket.util;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;

import com.trillion.tikitaka.global.security.domain.CustomUserDetails;

@Configuration
public class TicketFilterCacheKeyGenerator {

	@Bean("ticketFilterKeyGeneratorForManager")
	public KeyGenerator ticketFilterKeyGeneratorForManager() {
		return (target, method, params) -> {
			TicketFilter filter = (TicketFilter)params[0];
			return generateKey(filter);
		};
	}

	@Bean("ticketFilterKeyGeneratorForUser")
	public KeyGenerator ticketFilterKeyGeneratorForUser() {
		return (target, method, params) -> {
			TicketFilter filter = (TicketFilter)params[0];
			CustomUserDetails userDetails = (CustomUserDetails)params[1];
			return userDetails.getId() + ":" + generateKey(filter);
		};
	}

	public static String generateKey(TicketFilter filter) {
		StringBuilder keyBuilder = new StringBuilder();
		keyBuilder.append("page=").append(filter.getPageable().getPageNumber()).append("&");
		keyBuilder.append("size=").append(filter.getPageable().getPageSize()).append("&");
		keyBuilder.append("sort=").append(filter.getSort()).append("&");

		if (filter.getStatus() != null) {
			keyBuilder.append("status=").append(filter.getStatus().name()).append("&");
		}

		if (filter.getPriority() != null) {
			keyBuilder.append("priority=").append(filter.getPriority().name()).append("&");
		}

		if (filter.getManagerId() != null) {
			keyBuilder.append("managerId=").append(filter.getManagerId()).append("&");
		}

		if (filter.getTypeId() != null) {
			keyBuilder.append("typeId=").append(filter.getTypeId()).append("&");
		}

		if (filter.getPrimaryCategoryId() != null) {
			keyBuilder.append("primaryCategoryId=").append(filter.getPrimaryCategoryId()).append("&");
		}

		if (filter.getSecondaryCategoryId() != null) {
			keyBuilder.append("secondaryCategoryId=").append(filter.getSecondaryCategoryId()).append("&");
		}

		if (filter.getUrgent() != null) {
			keyBuilder.append("urgent=").append(filter.getUrgent()).append("&");
		}

		if (filter.getKeyword() != null) {
			keyBuilder.append("keyword=").append(filter.getKeyword());
		}

		// 필요시 해시를 적용하여 길이를 고정시킴
		return DigestUtils.md5DigestAsHex(keyBuilder.toString().getBytes());
	}
}
