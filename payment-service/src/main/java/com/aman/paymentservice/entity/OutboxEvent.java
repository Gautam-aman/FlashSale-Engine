package com.aman.paymentservice.entity;


import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

@Entity
@Table(
		name = "outbox_events",
		indexes = {
				@Index(
						name = "idx_outbox_processed",
						columnList = "processed"
				)
		}
)
public class OutboxEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private UUID eventId;

	@Getter @Column(nullable = false)
	private String eventType;

	@Getter @Column(nullable = false)
	private String aggregateId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String payload;

	@Column(nullable = false)
	private boolean processed = false;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	public OutboxEvent(UUID eventId, String eventType, String aggregateId, String payload) {
	}

	public OutboxEvent() {

	}

	public @Nullable String getPayload() {
		return payload;
	}

	public void markProcessed() {
		processed = true;
	}

	public Object getEventId() {
		return eventId;
	}
}
