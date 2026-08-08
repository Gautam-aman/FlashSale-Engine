package com.aman.bookingservice.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String venue;

	@Column(nullable = false)
	private LocalDateTime eventTime;

	@Column(nullable = false)
	private boolean active = true;

	@OneToMany(mappedBy = "event" , cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TicketType> ticketTypes = new ArrayList<>();

	public void addTicketType(TicketType ticketType) {
		ticketTypes.add(ticketType);
		ticketType.setEvent(this);
	}
}
