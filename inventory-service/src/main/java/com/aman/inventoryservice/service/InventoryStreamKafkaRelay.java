package com.aman.inventoryservice.service;

import java.util.List;
import java.util.Map;

import com.aman.inventoryservice.config.RedisStreamConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryStreamKafkaRelay {

	private static final Logger log = LoggerFactory.getLogger(InventoryStreamKafkaRelay.class);
	private final StringRedisTemplate redisTemplate;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Scheduled(fixedDelay = 1000)
	public void relayEvents() {
		List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(
								StreamOffset.create(
										RedisStreamConfig.INVENTORY_EVENTS,
										ReadOffset.lastConsumed()
								)
						);

		if (records == null || records.isEmpty()) {
			return;
		}
		for (MapRecord<String, Object, Object> record : records) {
			Map<Object, Object> value = record.getValue();

			String eventType = value.get("eventType").toString();
			String topic = getTopic(eventType);
			String reservationId = value.get("reservationId").toString();
			kafkaTemplate.send(
					topic,
					reservationId,
					value
			);
			log.info(
					"Relayed inventory event: " +
							"streamId={}, type={}, topic={}",
					record.getId(),
					eventType,
					topic
			);
		}
	}

	private String getTopic(String eventType) {
		return switch (eventType) {
			case "INVENTORY_RESERVED" -> "inventory.reserved";
			case "INVENTORY_UNAVAILABLE" -> "inventory.unavailable";
			default ->
					throw new IllegalArgumentException(
							"Unknown inventory event: "
									+ eventType
					);
		};
	}


}
