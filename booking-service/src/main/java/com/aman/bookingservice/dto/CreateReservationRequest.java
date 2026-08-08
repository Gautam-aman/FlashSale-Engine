package com.aman.bookingservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReservationRequest(
		@NotBlank
		String userId,

		@NotNull
		Long ticketTypeId,

		@NotNull
		@Min(1)
		Integer quantity
) {
}
