package com.aman.inventoryservice.service;


import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisInventoryService {

	private final StringRedisTemplate redisTemplate;

	public void releaseInventory(Long ticketTypeId, int quantity) {

		String key = "inventory:" + ticketTypeId;
		redisTemplate.opsForValue().increment(key, quantity);
	}

}
