package com.aman.bookingservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
	@GetMapping("/health")
	public String health() {
		return "FlashSale Booking Service is UP";
	}
}
