package com.aman.bookingservice.entity;


import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "outbox_events",
indexes = {
		@Index(
				name = "idx_outbox_processed",
				columnList = "processed"
		)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private UUID eventId;

	@Column(nullable = false)
	private String eventType;

	@Column(nullable = false)
	private String aggregateId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String payload;

	@Column(nullable = false)
	private boolean processed = false;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	public OutboxEvent(UUID uuid, String reservationCreated, String string, String payload) {
		this.eventId = uuid;
		this.eventType = reservationCreated;
		this.aggregateId = string;
		this.payload = payload;
		this.createdAt = LocalDateTime.now();
	}
}
