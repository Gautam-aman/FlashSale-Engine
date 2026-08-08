package com.aman.bookingservice.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEventRequest (
		@NotBlank
		String name,

		@NotBlank
		String venue,

		@NotNull
		@Future
		LocalDateTime eventTime,

		@NotNull
		List<CreateTicketTypeRequest> ticketTypes
){

}
