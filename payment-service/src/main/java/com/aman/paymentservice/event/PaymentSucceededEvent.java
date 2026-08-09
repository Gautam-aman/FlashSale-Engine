package com.aman.paymentservice.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentSucceededEvent(
		UUID eventId,
		UUID paymentId,
		UUID reservationId,
		String userId,
		LocalDateTime createdAt
) {
}
