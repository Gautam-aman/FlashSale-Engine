package com.aman.bookingservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "idempotency_records" ,
uniqueConstraints = {
		@UniqueConstraint(
				name = "uk_idempotency_key",
				columnNames = "idempotency_key"
		)
})
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyRecord {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "idempotency_key", nullable = false, unique = true)
	private String idempotencyKey;

	@Column(name = "request_hash", nullable = false)
	private String requestHash;

	@Column(name = "reservation_id")
	private String reservationId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private IdempotencyStatus status;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	public void markCompleted(String reservationId) {
		this.reservationId = reservationId;
		this.status = IdempotencyStatus.COMPLETED;
	}

	public void markFailed() {
		this.status = IdempotencyStatus.FAILED;
	}

}
