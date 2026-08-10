package com.aman.inventoryservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
		name = "processed_events",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_inventory_processed_event",
						columnNames = "event_id"
				)
		}
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(
			name = "event_id",
			nullable = false,
			unique = true
	)
	private UUID eventId;

	@Getter @Column(nullable = false)
	private LocalDateTime processedAt;

	public ProcessedEvent(UUID eventId) {
		this.eventId = eventId;
		this.processedAt = LocalDateTime.now();
	}

}
