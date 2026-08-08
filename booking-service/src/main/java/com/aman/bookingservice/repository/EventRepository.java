package com.aman.bookingservice.repository;

import com.aman.bookingservice.entity.Event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
