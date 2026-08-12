package com.aman.bookingservice.event;

import java.time.LocalDateTime;
import java.util.*;

public record InventoryReserveRequestedEvent(
		UUID eventId,
		UUID reservationId,
		Long ticketTypeId,
		Integer quantity,
		LocalDateTime createdAt
) {
}
