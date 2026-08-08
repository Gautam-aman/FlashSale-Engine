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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final TicketTypeRepository ticketTypeRepository;
	private final ReservationRepository reservationRepository;
	private final RedisInventoryService redisInventoryService;
	private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

	@Transactional
	public ReservationResponse createReservation(CreateReservationRequest request){

		log.info(
				"Attempting reservation: userId={}, ticketTypeId={}, quantity={}",
				request.userId(),
				request.ticketTypeId(),
				request.quantity()
		);

		TicketType ticketType = ticketTypeRepository.findById(request.ticketTypeId())
				.orElseThrow(() -> new IllegalArgumentException("Ticket type not found"));

		boolean reserved = redisInventoryService.reserveInventory(request.ticketTypeId(), request.quantity());

		if (!reserved) {
			throw new InsufficientInventoryException("Not enough tickets available");
		}



		try{


			log.info(
					"Inventory reserved in Redis: ticketTypeId={}, quantity={}",
					ticketType.getId(),
					request.quantity()
			);

			LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

			Reservation reservation = new Reservation(request.userId(), ticketType, request.quantity(), expiresAt);
			reservationRepository.save(reservation);

			log.info(
					"Reservation created: reservationId={}, userId={}",
					reservation.getReservationId(),
					request.userId()
			);

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
			log.error(
					"Failed to persist reservation, compensating Redis inventory: ticketTypeId={}, quantity={}",
					ticketType.getId(),
					request.quantity(),
					e
			);
			throw e;
		}


	}

}
