package com.aman.bookingservice.repository;

import java.util.List;

import com.aman.bookingservice.entity.OutboxEvent;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
	List<OutboxEvent> findTop100ByProcessedFalseOrderByIdAsc();
}
