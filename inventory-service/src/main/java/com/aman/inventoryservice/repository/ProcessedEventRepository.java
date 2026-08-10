package com.aman.inventoryservice.repository;


import java.util.UUID;

import com.aman.inventoryservice.entity.ProcessedEvent;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {

	boolean existsByEventId(UUID eventId);
}
