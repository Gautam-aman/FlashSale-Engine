package com.aman.bookingservice.repository;

import com.aman.bookingservice.entity.TicketType;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
}
