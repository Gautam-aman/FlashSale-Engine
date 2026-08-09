package com.aman.reservationservice.repository;

import java.util.UUID;

import com.aman.reservationservice.event.ProcessedEvent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {
	boolean existsByEventId(UUID eventId);
}
