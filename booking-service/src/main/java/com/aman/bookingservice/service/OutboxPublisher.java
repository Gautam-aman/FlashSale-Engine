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
			kafkaTemplate.send("reservation.created",
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

}
