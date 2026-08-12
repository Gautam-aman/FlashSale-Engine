package com.aman.inventoryservice.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryUnavailableEvent (
		UUID eventId,
		UUID reservationId,
		Long ticketTypeId,
		Integer quantity,
		String reason,
		LocalDateTime createdAt
){
}
