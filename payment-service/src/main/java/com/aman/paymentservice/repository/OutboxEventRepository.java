package com.aman.paymentservice.repository;

import java.util.List;

import com.aman.paymentservice.entity.OutboxEvent;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

	List<OutboxEvent> findTop100ByProcessedFalseOrderByIdAsc();

}
