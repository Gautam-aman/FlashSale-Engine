package com.aman.bookingservice.service;

import java.util.List;

import com.aman.bookingservice.entity.OutboxEvent;
import com.aman.bookingservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxPublisher {

	private final OutboxEventRepository outboxEventRepository;
	private final KafkaTemplate<String, String> kafkaTemplate;

	@Scheduled(fixedDelay = 1000)
	public void publishEvents(){
		List<OutboxEvent> events = outboxEventRepository.findTop100ByProcessedFalseOrderByIdAsc();

		for(OutboxEvent event : events){
			String topic = getTopic(event.getEventType());

			kafkaTemplate.send(topic,
					event.getAggregateId(),
					event.getPayload())
					.whenComplete((res, ex) -> {
						if(ex == null){
							event.setProcessed(true);
							outboxEventRepository.save(event);
						}
					});
		}

	}

	private String getTopic(String eventType) {
		return switch (eventType) {
			case "RESERVATION_CREATED" ->
					"reservation.created";
			case "INVENTORY_RELEASE_REQUESTED" ->
					"inventory.release.requested";
			default ->
					throw new IllegalArgumentException(
							"Unknown event type: "
									+ eventType
					);
		};
	}

}
