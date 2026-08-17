package com.aman.inventoryservice.service;

import com.aman.inventoryservice.config.RedisStreamConfig;
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
	private final DefaultRedisScript<Long> releaseInventoryScript;
	private final DefaultRedisScript<Long> reserveInventoryScript;

	public boolean releaseInventory(Long ticketTypeId, int quantity, String eventId) {

		String inventoryKey = "inventory:" + ticketTypeId;
		String processedKey = "inventory:release:processed:" + eventId;
		Long result = redisTemplate.execute(
						releaseInventoryScript,
						List.of(
								inventoryKey,
								processedKey
						),
						String.valueOf(quantity)
				);
		return result != null && result == 1;
	}

	public boolean reserveInventory(Long ticketTypeId, int quantity, String eventId, String reservationId, String userId, String amount) {
		String inventoryKey = "inventory:" + ticketTypeId;

		Long result = redisTemplate.execute(reserveInventoryScript,
						List.of(
								inventoryKey,
								RedisStreamConfig.INVENTORY_EVENTS
						),
						String.valueOf(quantity),
						eventId,
						reservationId,
						userId,
						String.valueOf(ticketTypeId),
						amount
				);
		return result != null && result == 1;
	}

}