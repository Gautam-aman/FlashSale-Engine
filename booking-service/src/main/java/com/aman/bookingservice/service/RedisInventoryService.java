package com.aman.bookingservice.service;

import java.util.Collections;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisInventoryService {
	private static final String INVENTORY_KEY_PREFIX = "ticket:inventory:";

	private final StringRedisTemplate redisTemplate = new StringRedisTemplate();
	private final DefaultRedisScript<Long> reserveInventoryScript;

	public void initializeInventory(Long ticketTypeId, int quantity){
		String key = getInventoryKey(ticketTypeId);
		redisTemplate.opsForValue().set(key, String.valueOf(quantity));
	}

	private String getInventoryKey(Long ticketTypeId) {
		return INVENTORY_KEY_PREFIX + ticketTypeId;
	}

	public long getInventory(Long ticketTypeId) {
		String key = getInventoryKey(ticketTypeId);
		String value = redisTemplate.opsForValue().get(key);
		if (value == null) {return -1;}
		return Long.parseLong(value);
	}

	public boolean reserveInventory(Long ticketTypeId, int quantity){
		String key = getInventoryKey(ticketTypeId);
		Long result = redisTemplate.execute(reserveInventoryScript,
				Collections.singletonList(key),
				String.valueOf(quantity)
		);
		return result != null && result == 1;
	}

	public void releaseInventory(Long ticketTypeId, int quantity) {
		String key = getInventoryKey(ticketTypeId);
		redisTemplate.opsForValue().increment(key, quantity);
	}

}
