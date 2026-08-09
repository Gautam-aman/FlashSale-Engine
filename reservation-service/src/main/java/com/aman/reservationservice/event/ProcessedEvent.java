package com.aman.reservationservice.event;


import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.springframework.scheduling.annotation.EnableScheduling;

@Entity
@Table(name = "processed_events" ,
uniqueConstraints = {
		@UniqueConstraint(
				name = "uk_processed_event_id",
				columnNames = "event_id"
		)
})
public class ProcessedEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "event_id", nullable = false, unique = true)
	private UUID eventId;

}
