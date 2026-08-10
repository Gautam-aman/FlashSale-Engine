package com.aman.paymentservice.service;

import java.util.List;

import com.aman.paymentservice.entity.OutboxEvent;
import com.aman.paymentservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentOutboxPublisher {

	private static final Logger log = LoggerFactory.getLogger(PaymentOutboxPublisher.class);
	private final OutboxEventRepository outboxEventRepository;
	private final KafkaTemplate<String, String> kafkaTemplate;

	@Scheduled(fixedDelay = 1000)
	public void publishEvents() {

		List<OutboxEvent> events = outboxEventRepository.findTop100ByProcessedFalseOrderByIdAsc();
		for (OutboxEvent event : events) {
			String topic = getTopic(event.getEventType());

			kafkaTemplate.send(
							topic,
							event.getAggregateId(),
							event.getPayload()
					)
					.whenComplete((result, exception) -> {
								if (exception == null) {
									event.markProcessed();
									outboxEventRepository.save(event);

									log.info("Published outbox event: " + "eventId={}, topic={}",
											event.getEventId(),
											topic
									);

								} else {
									log.error("Failed to publish outbox event: " + "eventId={}",
											event.getEventId(),
											exception
									);
								}
							}
					);
		}
	}

	private String getTopic(String eventType) {
		return switch (eventType) {
			case "PAYMENT_SUCCEEDED" ->
					"payment.succeeded";
			case "PAYMENT_FAILED" ->
					"payment.failed";
			default -> throw new IllegalArgumentException(
							"Unknown event type: "
									+ eventType
					);
		};
	}

}
