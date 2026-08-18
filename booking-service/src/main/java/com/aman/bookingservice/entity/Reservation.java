package com.aman.bookingservice.entity;


import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UuidGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
		name = "reservations",
		indexes ={
				@Index(name = "idx_reservation_ticket_type", columnList = "ticket_type_id"),
				@Index(name = "idx_reservation_user", columnList = "user_id")
		}
)
public class Reservation {

	@Id
	@UuidGenerator
	private UUID id;

	@Column(nullable = false, unique = true)
	private UUID reservationId;

	@Column(nullable = false)
	private String userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_type_id", nullable = false)
	private TicketType ticketType;

	@Column(nullable = false)
	private Integer quantity;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReservationStatus status;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	public Reservation(@NotBlank String s, TicketType ticketType, @NotNull @Min(1) Integer quantity, java.time.LocalDateTime expiresAt) {
		this.reservationId = UUID.randomUUID();
		this.userId = s;
		this.ticketType = ticketType;
		this.quantity = quantity;
		this.status = ReservationStatus.PENDING;
		this.createdAt = LocalDateTime.now();
		this.expiresAt = expiresAt;
	}

	public void confirm() {
		if (this.status != ReservationStatus.PENDING) {
			throw new IllegalStateException(
					"Only pending reservations can be confirmed"
			);
		}

		this.status = ReservationStatus.CONFIRMED;
	}

	public void cancel() {
		if (this.status != ReservationStatus.PENDING) {
			throw new IllegalStateException(
					"Only pending reservations can be cancelled"
			);
		}

		this.status = ReservationStatus.CANCELLED;
	}

}
