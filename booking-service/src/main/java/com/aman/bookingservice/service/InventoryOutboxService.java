package com.aman.bookingservice.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.aman.bookingservice.entity.OutboxEvent;
import com.aman.bookingservice.entity.Reservation;
import com.aman.bookingservice.event.InventoryReleaseRequestedEvent;
import com.aman.bookingservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryOutboxService {

	private final OutboxEventRepository outboxEventRepository;
	private final ObjectMapper objectMapper;

	public void createInventoryReleaseEvent(Reservation reservation) {

		InventoryReleaseRequestedEvent event = new InventoryReleaseRequestedEvent(
						UUID.randomUUID(),
						reservation.getReservationId(),
						reservation.getTicketType().getId(),
						reservation.getQuantity(),
						LocalDateTime.now()
				);
		try {
			String payload = objectMapper.writeValueAsString(event);
			OutboxEvent outboxEvent =
					new OutboxEvent(
							event.eventId(),
							"INVENTORY_RELEASE_REQUESTED",
							reservation.getReservationId().toString(),
							payload
					);
			outboxEventRepository.save(outboxEvent);
		}
		catch (Exception e) {
			throw new IllegalStateException("Failed to serialize inventory release event", e);
		}
	}
}
