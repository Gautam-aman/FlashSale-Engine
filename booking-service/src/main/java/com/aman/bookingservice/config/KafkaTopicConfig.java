package com.aman.bookingservice.config;

import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

	@Bean
	public NewTopic reservationCreatedTopic() {
		return new NewTopic("reservation.created", 3, (short) 1);
	}

}
