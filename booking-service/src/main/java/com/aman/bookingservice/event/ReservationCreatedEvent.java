package com.aman.bookingservice.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationCreatedEvent(
		UUID eventId,
		UUID reservationId,
		String userId,
		Long ticketTypeId,
		Integer quantity,
		LocalDateTime createdAt
) {
}
