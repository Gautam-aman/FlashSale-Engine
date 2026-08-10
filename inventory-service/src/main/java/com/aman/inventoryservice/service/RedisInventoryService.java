package com.aman.inventoryservice.service;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ClassPathResource;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisInventoryService {

	private final StringRedisTemplate redisTemplate;
	//private final DefaultRedisScript<Long> releaseInventoryScript();

	public boolean releaseInventory(Long ticketTypeId, int quantity, String eventId) {

		String inventoryKey = "inventory:" + ticketTypeId;
		String processedKey = "inventory:release:processed:" + eventId;

		Long result = redisTemplate.execute(releaseInventoryScript(), List.of(inventoryKey, processedKey),
						String.valueOf(quantity)
				);

		return result != null && result == 1;
	}

	private DefaultRedisScript<Long> releaseInventoryScript() {
		DefaultRedisScript<Long> script = new DefaultRedisScript<>();
		script.setLocation(
				new ClassPathResource(
						"scripts/release_inventory.lua"
				)
		);
		script.setResultType(Long.class);
		return script;
	}


}