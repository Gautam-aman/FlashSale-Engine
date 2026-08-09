package com.aman.paymentservice.repository;

import java.util.Optional;
import java.util.UUID;

import com.aman.paymentservice.entity.Payment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Optional<Payment> findByReservationId(UUID reservationId);
}
