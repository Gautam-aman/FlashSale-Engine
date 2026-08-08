package com.aman.bookingservice.repository;

import java.util.Optional;

import com.aman.bookingservice.entity.IdempotencyRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, Long> {
	Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);
}
