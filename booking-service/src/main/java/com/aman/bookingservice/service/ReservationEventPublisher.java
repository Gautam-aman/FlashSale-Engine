package com.aman.bookingservice.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.aman.bookingservice.entity.Reservation;
import com.aman.bookingservice.event.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationEventPublisher {

	private static final String TOPIC = "reservation.created";
	private final KafkaTemplate<String, ReservationCreatedEvent> kafkaTemplate;

	public void publishReservationCreated(Reservation reservation) {

		ReservationCreatedEvent event = new ReservationCreatedEvent(
						UUID.randomUUID(),
						reservation.getReservationId(),
						reservation.getUserId(),
						reservation.getTicketType().getId(),
						reservation.getQuantity(),
						LocalDateTime.now()
				);

		kafkaTemplate.send(TOPIC, reservation.getReservationId().toString(), event);
	}

}
