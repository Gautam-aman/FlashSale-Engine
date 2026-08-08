package com.aman.bookingservice.controller;

import com.aman.bookingservice.dto.CreateReservationRequest;
import com.aman.bookingservice.dto.ReservationResponse;
import com.aman.bookingservice.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

	private final ReservationService reservationService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ReservationResponse createReservation(@Valid @RequestBody CreateReservationRequest request ,
			@RequestHeader("Idempotency-Key") String idempotencyKey) {
		return reservationService.createReservation(idempotencyKey ,request);
	}

}
