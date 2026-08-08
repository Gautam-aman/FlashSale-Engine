package com.aman.bookingservice.entity;


import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ticket_types")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Column(nullable = false)
	private Integer totalQuantity;

	@Column(nullable = false)
	private Integer availableQuantity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@Version
	@Column(nullable = false)
	private Long version;

	public void decreaseInventory(int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("Quantity must be positive");
		}
		if (availableQuantity < quantity) {
			throw new IllegalStateException("Not enough tickets available");
		}
		availableQuantity -= quantity;
	}

	public void increaseInventory(int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("Quantity must be positive");
		}
		availableQuantity += quantity;
	}

	public TicketType(@NotBlank String name, @NotNull @DecimalMin("0.01") BigDecimal price, @NotNull @Min(1) Integer quantity) {
	}
}
