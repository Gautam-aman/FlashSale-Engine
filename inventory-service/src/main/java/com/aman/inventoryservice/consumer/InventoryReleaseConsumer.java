package com.aman.inventoryservice.consumer;


import com.aman.inventoryservice.entity.ProcessedEvent;
import com.aman.inventoryservice.event.InventoryReleaseRequestedEvent;
import com.aman.inventoryservice.repository.ProcessedEventRepository;
import com.aman.inventoryservice.service.RedisInventoryService;
import jakarta.transaction.Transactional;
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

	private final ProcessedEventRepository processedEventRepository;

	@KafkaListener(
			topics = "inventory.release.requested",
			groupId = "inventory-service"
	)
	@Transactional
	public void consume(InventoryReleaseRequestedEvent event) {
		log.info(
				"Received inventory release request: " +
						"eventId={}, reservationId={}",
				event.eventId(),
				event.reservationId()
		);
		if (processedEventRepository.existsByEventId(event.eventId())) {
			log.info(
					"Event already processed: eventId={}",
					event.eventId()
			);
			return;
		}
		redisInventoryService.releaseInventory(event.ticketTypeId(), event.quantity() , String.valueOf(event.eventId()));
		processedEventRepository.save(new ProcessedEvent(event.eventId()));
		log.info(
				"Inventory released successfully: " +
						"ticketTypeId={}, quantity={}",
				event.ticketTypeId(),
				event.quantity()
		);
	}
}
