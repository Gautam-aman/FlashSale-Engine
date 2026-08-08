package com.aman.bookingservice.controller;

import com.aman.bookingservice.dto.CreateEventRequest;
import com.aman.bookingservice.entity.Event;
import com.aman.bookingservice.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

	private final EventService eventService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Event createEvent(@Valid @RequestBody CreateEventRequest request) {
		return eventService.createEvent(request);
	}

}
