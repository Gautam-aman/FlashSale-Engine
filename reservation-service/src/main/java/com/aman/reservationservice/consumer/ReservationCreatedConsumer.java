package com.aman.reservationservice.consumer;

import com.aman.reservationservice.event.ProcessedEvent;
import com.aman.reservationservice.event.ReservationCreatedEvent;
import com.aman.reservationservice.repository.ProcessedEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationCreatedConsumer {
	private final ProcessedEventRepository processedEventRepository;

	@RetryableTopic(
			attempts = "4",
			exclude = {
					IllegalArgumentException.class
			},
			backOff = @BackOff(
					delay = 2000,
					multiplier = 2.0 ,
					maxDelay = 1000
			)
	)
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

		if (event.userId().equals("FAIL")) {
			throw new RuntimeException(
					"Simulated downstream failure"
			);
		}

		ProcessedEvent processedEvent = new ProcessedEvent(event.eventId());
		processedEventRepository.save(processedEvent);
		System.out.println("Successfully processed event: " + event.eventId());

	}

	@DltHandler
	public void handleDlt(ReservationCreatedEvent event){

		System.err.println("Reservation event moved to DLT: " + event.eventId());
		System.err.println("Reservation ID: " + event.reservationId());

	}

}
