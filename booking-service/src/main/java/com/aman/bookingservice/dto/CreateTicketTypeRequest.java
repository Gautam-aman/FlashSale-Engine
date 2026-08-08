package com.aman.bookingservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTicketTypeRequest (
		@NotBlank
		String name,

		@NotNull
		@DecimalMin("0.01")
		BigDecimal price,

		@NotNull
		@Min(1)
		Integer quantity
){

}
