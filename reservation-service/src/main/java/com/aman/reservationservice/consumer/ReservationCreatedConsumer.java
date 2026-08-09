package com.aman.reservationservice.consumer;

import com.aman.reservationservice.event.ProcessedEvent;
import com.aman.reservationservice.event.ReservationCreatedEvent;
import com.aman.reservationservice.repository.ProcessedEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationCreatedConsumer {
	private final ProcessedEventRepository processedEventRepository;

	@KafkaListener(
			topics = "reservation.created",
			groupId = "reservation-service"
	)
	@Transactional
	public void consume(ReservationCreatedEvent event){
		System.out.println("Received reservation event: " + event.reservationId());

		if (processedEventRepository.existsByEventId(event.eventId())) {
			System.out.println(
					"Event already processed: " + event.eventId()
			);
			return;
		}

		// Business will go here

		ProcessedEvent processedEvent = new ProcessedEvent(event.eventId());
		processedEventRepository.save(processedEvent);
		System.out.println("Successfully processed event: " + event.eventId());

	}

}
