package com.aman.paymentservice.event;

import java.time.LocalDateTime;
import java.util.*;

public record PaymentFailedEvent (
		UUID eventId,
		UUID paymentId,
		UUID reservationId,
		String userId,
		String reason,
		LocalDateTime createdAt
){
}
