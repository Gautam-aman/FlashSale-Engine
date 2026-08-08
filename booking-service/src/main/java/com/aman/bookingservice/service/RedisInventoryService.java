package com.aman.bookingservice.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisInventoryService {
	private static final String INVENTORY_KEY_PREFIX = "ticket:inventory:";
}
