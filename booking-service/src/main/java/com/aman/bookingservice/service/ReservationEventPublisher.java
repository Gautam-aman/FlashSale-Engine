package com.aman.bookingservice.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.aman.bookingservice.entity.Reservation;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationEventPublisher {

	private static final String TOPIC = "reservation.created";
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void publishReservationCreated(Reservation reservation) {

		com.aman.bookingservice.event.ReservationCreatedEvent event = new com.aman.bookingservice.event.ReservationCreatedEvent(
						UUID.randomUUID(),
						reservation.getReservationId(),
						reservation.getUserId(),
						reservation.getTicketType().getId(),
						reservation.getQuantity(),
						LocalDateTime.now(),
						reservation.getTicketType().getPrice()
								.multiply(java.math.BigDecimal.valueOf(reservation.getQuantity()))
				);

		kafkaTemplate.send(TOPIC, reservation.getReservationId().toString(), event);
	}

}
