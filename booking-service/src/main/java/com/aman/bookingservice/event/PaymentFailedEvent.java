package com.aman.bookingservice.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentFailedEvent(
		UUID eventId,
		UUID paymentId,
		UUID reservationId,
		String userId,
		String reason,
		LocalDateTime createdAt
) {
}
