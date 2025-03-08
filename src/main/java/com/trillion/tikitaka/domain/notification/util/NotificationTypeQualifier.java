package com.trillion.tikitaka.domain.notification.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.trillion.tikitaka.domain.notification.domain.NotificationType;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotificationTypeQualifier {
	NotificationType value();
}
