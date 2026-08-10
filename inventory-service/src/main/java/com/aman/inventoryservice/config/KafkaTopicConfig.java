package com.aman.inventoryservice.config;

import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {


	@Bean
	public NewTopic inventoryReleaseTopic() {
		return new NewTopic(
				"inventory.release.requested",
				3,
				(short) 1
		);
	}

}
