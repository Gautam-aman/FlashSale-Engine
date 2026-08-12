package com.aman.bookingservice.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryReservedEvent (
		UUID eventId,
		UUID reservationId,
		Long ticketTypeId,
		Integer quantity,
		LocalDateTime createdAt
){
}
