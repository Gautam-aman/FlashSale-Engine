package com.aman.paymentservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
		name = "payments",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_payment_reservation",
						columnNames = "reservation_id"
				)
		}
)
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(
			name = "payment_id",
			nullable = false,
			unique = true
	)
	private UUID paymentId;

	@Column(
			name = "reservation_id",
			nullable = false
	)
	private UUID reservationId;

	@Column(nullable = false)
	private String userId;

	@Column(nullable = false)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	public Payment(UUID uuid, String s, BigDecimal amount) {
	}

	public void markSuccess() {
		status = PaymentStatus.SUCCESS;
	}
}
