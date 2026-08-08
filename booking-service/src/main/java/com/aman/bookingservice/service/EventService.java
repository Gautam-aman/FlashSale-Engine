package com.aman.bookingservice.service;

import java.beans.Transient;

import com.aman.bookingservice.dto.CreateEventRequest;
import com.aman.bookingservice.dto.CreateTicketTypeRequest;
import com.aman.bookingservice.entity.Event;
import com.aman.bookingservice.entity.TicketType;
import com.aman.bookingservice.repository.EventRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

	private final EventRepository eventRepository;

	@Transient
	public Event createEvent(CreateEventRequest eventRequest) {
		Event event = Event.builder()
				.name(eventRequest.name())
				.venue(eventRequest.venue())
				.eventTime(eventRequest.eventTime())
				.build();

		for (CreateTicketTypeRequest ticketTypeRequest : eventRequest.ticketTypes()) {
			TicketType ticketType = new TicketType(
					ticketTypeRequest.name(),
					ticketTypeRequest.price(),
					ticketTypeRequest.quantity()
			);
			event.addTicketType(ticketType);
		}
		return eventRepository.save(event);
	}


}
