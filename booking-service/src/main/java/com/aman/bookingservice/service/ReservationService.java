package com.aman.bookingservice.service;

import java.beans.Transient;
import java.time.LocalDateTime;

import com.aman.bookingservice.dto.CreateReservationRequest;
import com.aman.bookingservice.dto.ReservationResponse;
import com.aman.bookingservice.entity.Reservation;
import com.aman.bookingservice.entity.TicketType;
import com.aman.bookingservice.exception.InsufficientInventoryException;
import com.aman.bookingservice.repository.ReservationRepository;
import com.aman.bookingservice.repository.TicketTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final TicketTypeRepository ticketTypeRepository;
	private final ReservationRepository reservationRepository;
	private final RedisInventoryService redisInventoryService;

	@Transactional
	public ReservationResponse createReservation(CreateReservationRequest request){

		boolean reserved = redisInventoryService.reserveInventory(request.ticketTypeId(), request.quantity());

		if (!reserved) {
			throw new InsufficientInventoryException("Not enough tickets available");
		}

		try{
			TicketType ticketType = ticketTypeRepository.findById(request.ticketTypeId())
					.orElseThrow(() -> new IllegalArgumentException("Ticket type not found"));

			LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

			Reservation reservation = new Reservation(request.userId(), ticketType, request.quantity(), expiresAt);
			reservationRepository.save(reservation);
			return new ReservationResponse(
					reservation.getReservationId(),
					reservation.getUserId(),
					ticketType.getId(),
					reservation.getQuantity(),
					reservation.getStatus(),
					reservation.getCreatedAt(),
					reservation.getExpiresAt()
			);

		}
		catch(Exception e){
			redisInventoryService.releaseInventory(request.ticketTypeId(), request.quantity());
			throw e;
		}


	}

}
