package com.aman.inventoryservice.consumer;

import com.aman.inventoryservice.event.InventoryReserveRequestedEvent;
import com.aman.inventoryservice.service.RedisInventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryReserveConsumer {

	private static final Logger log = LoggerFactory.getLogger(InventoryReserveConsumer.class);
	private final RedisInventoryService redisInventoryService;

	@KafkaListener(topics = "inventory.reserve.requested", groupId = "inventory-service")
	public void consume(InventoryReserveRequestedEvent event) {
		log.info(
				"Received inventory reservation request: " +
						"reservationId={}, quantity={}",
				event.reservationId(),
				event.quantity()
		);

		boolean reserved = redisInventoryService.reserveInventory(
						event.ticketTypeId(),
						event.quantity(),
						event.eventId().toString(),
						event.reservationId().toString(),
						event.userId(),
						event.amount().toString()
				);
		if (reserved) {
			log.info(
					"Inventory reserved: reservationId={}",
					event.reservationId()
			);
		} else {
			log.warn(
					"Inventory unavailable: reservationId={}",
					event.reservationId()
			);
		}
	}
}
