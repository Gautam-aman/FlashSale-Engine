package com.aman.bookingservice.repository;

import java.util.Optional;
import java.util.UUID;

import com.aman.bookingservice.entity.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	Optional<Reservation> findByReservationId(UUID reservationId);
}
