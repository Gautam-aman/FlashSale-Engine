package com.aman.bookingservice.service;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.aman.bookingservice.dto.CreateReservationRequest;
import com.aman.bookingservice.dto.ReservationResponse;
import com.aman.bookingservice.entity.IdempotencyRecord;
import com.aman.bookingservice.entity.IdempotencyStatus;
import com.aman.bookingservice.entity.Reservation;
import com.aman.bookingservice.entity.TicketType;
import com.aman.bookingservice.exception.InsufficientInventoryException;
import com.aman.bookingservice.repository.IdempotencyRecordRepository;
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
	private final IdempotencyRecordRepository idempotencyRecordRepository;
	private final RequestHashService requestHashService;
	private final ReservationEventPublisher reservationEventPublisher;

	@Transactional
	public ReservationResponse createReservation(String idempotencyKey ,CreateReservationRequest request){

		log.info(
				"Attempting reservation: userId={}, ticketTypeId={}, quantity={}",
				request.userId(),
				request.ticketTypeId(),
				request.quantity()
		);

		String requestHash =requestHashService.hash(request);
		Optional<IdempotencyRecord> existing = idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey);

		if (existing.isPresent()) {
			IdempotencyRecord record = existing.get();
			if (!record.getRequestHash().equals(requestHash)) {
				throw new IllegalArgumentException(
						"Idempotency key reused with different request"
				);
			}
			if (record.getStatus() == IdempotencyStatus.COMPLETED) {
				return getExistingReservation(
						record.getReservationId()
				);
			}

			if (record.getStatus() == IdempotencyStatus.PROCESSING) {
				throw new IllegalStateException(
						"Request with this idempotency key is already being processed"
				);
			}

			if (record.getStatus() == IdempotencyStatus.FAILED) {
				record.markProcessing();
				idempotencyRecordRepository.save(record);
			}
		}
		else{
			IdempotencyRecord record = new IdempotencyRecord(idempotencyKey, requestHash);
			idempotencyRecordRepository.save(record);
		}

		TicketType ticketType = ticketTypeRepository.findById(request.ticketTypeId())
				.orElseThrow(() -> new IllegalArgumentException("Ticket type not found"));

		boolean reserved = redisInventoryService.reserveInventory(request.ticketTypeId(), request.quantity());

		if (!reserved) {
			markFailed(idempotencyKey);
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

			reservationEventPublisher.publishReservationCreated(reservation);

			IdempotencyRecord record = idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey).orElseThrow(() ->
									new IllegalStateException("Idempotency record not found"));
			record.markCompleted(reservation.getReservationId().toString());
			idempotencyRecordRepository.save(record);

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
			markFailed(idempotencyKey);
			log.error(
					"Failed to persist reservation, compensating Redis inventory: ticketTypeId={}, quantity={}",
					ticketType.getId(),
					request.quantity(),
					e
			);
			throw e;
		}
	}

	private void markFailed(String idempotencyKey) {
		idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey)
				.ifPresent(record -> {
					record.markFailed();
					idempotencyRecordRepository.save(record);
				});
	}

	private ReservationResponse getExistingReservation(String reservationId) {

		Reservation reservation = reservationRepository.findByReservationId(UUID.fromString(reservationId))
						.orElseThrow(() -> new IllegalStateException("Reservation not found"));
		return new ReservationResponse(
				reservation.getReservationId(),
				reservation.getUserId(),
				reservation.getTicketType().getId(),
				reservation.getQuantity(),
				reservation.getStatus(),
				reservation.getCreatedAt(),
				reservation.getExpiresAt()
		);
	}

}
