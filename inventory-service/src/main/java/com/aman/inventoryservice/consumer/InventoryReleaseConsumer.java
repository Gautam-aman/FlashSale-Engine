package com.aman.inventoryservice.consumer;


import com.aman.inventoryservice.event.InventoryReleaseRequestedEvent;
import com.aman.inventoryservice.service.RedisInventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryReleaseConsumer {

	private static final Logger log = LoggerFactory.getLogger(InventoryReleaseConsumer.class);
	private final RedisInventoryService redisInventoryService;


	@KafkaListener(
			topics = "inventory.release.requested",
			groupId = "inventory-service"
	)
	public void consume(InventoryReleaseRequestedEvent event) {
		log.info(
				"Received inventory release request: " +
						"reservationId={}, quantity={}",
				event.reservationId(),
				event.quantity()
		);

		redisInventoryService.releaseInventory(event.ticketTypeId(), event.quantity());
		log.info(
				"Inventory released: ticketTypeId={}, quantity={}",
				event.ticketTypeId(),
				event.quantity()
		);
	}

}
