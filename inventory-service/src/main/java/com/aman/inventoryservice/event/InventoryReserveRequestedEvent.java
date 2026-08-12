package com.aman.inventoryservice.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryReserveRequestedEvent(
		UUID eventId,
		UUID reservationId,
		Long ticketTypeId,
		Integer quantity,
		LocalDateTime createdAt
) {
}
