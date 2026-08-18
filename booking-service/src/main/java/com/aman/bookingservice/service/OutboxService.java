package com.aman.bookingservice.service;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.UUID;

import com.aman.bookingservice.entity.OutboxEvent;
import com.aman.bookingservice.entity.Reservation;
import com.aman.bookingservice.event.ReservationCreatedEvent;
import com.aman.bookingservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {

	private final OutboxEventRepository outboxEventRepository;
	private final ObjectMapper objectMapper;

	public void createReservationCreatedEvent(Reservation reservation){

		try{

			BigDecimal amount = reservation.getTicketType().getPrice().multiply(BigDecimal.valueOf(reservation.getQuantity()));
			ReservationCreatedEvent event =
					new ReservationCreatedEvent(
							UUID.randomUUID(),
							reservation.getReservationId(),
							reservation.getUserId(),
								reservation.getTicketType().getId(),
								reservation.getQuantity(),
								LocalDateTime.now(),
								amount
						);

			String payload = objectMapper.writeValueAsString(event);
			OutboxEvent outboxEvent = new OutboxEvent(
							event.eventId(),
							"RESERVATION_CREATED",
							reservation.getReservationId().toString(),
							payload
					);
			outboxEventRepository.save(outboxEvent);
		}
		catch(Exception e){
			throw new IllegalStateException("Failed to serialize reservation event", e);
		}

	}

}
