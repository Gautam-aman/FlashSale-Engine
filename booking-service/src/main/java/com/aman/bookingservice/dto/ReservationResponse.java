package com.aman.bookingservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.aman.bookingservice.entity.ReservationStatus;

public record ReservationResponse (
		UUID reservationId,
		String userId,
		Long ticketTypeId,
		Integer quantity,
		ReservationStatus status,
		LocalDateTime createdAt,
		LocalDateTime expiresAt
){
}
