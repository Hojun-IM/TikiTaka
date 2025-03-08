package com.trillion.tikitaka.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

	public static final String EXCHANGE_NAME = "notification.exchange";

	public static final String KAKAOWORK_QUEUE = "kakao.queue";
	public static final String EMAIL_QUEUE = "email.queue";

	@Bean
	public TopicExchange notificationExchange() {
		return new TopicExchange(EXCHANGE_NAME);
	}

	@Bean
	public Queue kakaoQueue() {
		return new Queue(KAKAOWORK_QUEUE, true);
	}

	@Bean
	public Queue emailQueue() {
		return new Queue(EMAIL_QUEUE, true);
	}

	@Bean
	public Binding kakaoBinding(Queue kakaoQueue, TopicExchange exchange) {
		return BindingBuilder.bind(kakaoQueue)
			.to(exchange)
			.with("notification.kakaowork");
	}

	@Bean
	public Binding emailBinding(Queue emailQueue, TopicExchange exchange) {
		return BindingBuilder.bind(emailQueue)
			.to(exchange)
			.with("notification.email");
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);

		return factory;
	}
}
