package com.aman.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

	@Bean
	public NewTopic paymentSucceededTopic() {
		return new NewTopic(
				"payment.succeeded",
				3,
				(short) 1
		);
	}

	@Bean
	public NewTopic paymentFailedTopic() {
		return new NewTopic(
				"payment.failed",
				3,
				(short) 1
		);
	}

}
