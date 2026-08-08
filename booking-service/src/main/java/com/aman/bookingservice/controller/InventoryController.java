package com.aman.bookingservice.controller;

import com.aman.bookingservice.service.RedisInventoryService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Deprecated
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

	private final RedisInventoryService inventoryService;

	@GetMapping("/{ticketTypeId}")
	public long getInventory(@PathVariable Long ticketTypeId) {
		return inventoryService.getInventory(ticketTypeId);
	}

	@PostMapping("/{ticketTypeId}/reserve")
	public String reserve(@PathVariable Long ticketTypeId, @RequestParam int quantity) {
		boolean success = inventoryService.reserveInventory(ticketTypeId, quantity);
		return success ? "RESERVED" : "SOLD_OUT";
	}

	@PostMapping("/{ticketTypeId}/release")
	public String release(@PathVariable Long ticketTypeId, @RequestParam int quantity) {
		inventoryService.releaseInventory(ticketTypeId, quantity);
		return "RELEASED";
	}

}
