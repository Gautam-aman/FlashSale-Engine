package com.aman.bookingservice.consumer;

import com.aman.bookingservice.entity.Reservation;
import com.aman.bookingservice.entity.ReservationStatus;
import com.aman.bookingservice.event.PaymentFailedEvent;
import com.aman.bookingservice.repository.ReservationRepository;
import com.aman.bookingservice.service.InventoryOutboxService;
import com.aman.bookingservice.service.RedisInventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFailedConsumer {

	private static final Logger log = LoggerFactory.getLogger(PaymentFailedConsumer.class);

	private final ReservationRepository reservationRepository;
	private final RedisInventoryService redisInventoryService;
	private final InventoryOutboxService inventoryOutboxService;

	@KafkaListener(
			topics = "payment.failed",
			groupId = "booking-payment-service"
	)
	@Transactional
	public void consume(PaymentFailedEvent event) {
		log.warn(
				"Received payment.failed: reservationId={}, reason={}",
				event.reservationId(),
				event.reason()
		);

		Reservation reservation = reservationRepository.findByReservationId(event.reservationId())
						.orElseThrow(() -> new IllegalStateException("Reservation not found: " + event.reservationId()));

		if (reservation.getStatus() == ReservationStatus.CANCELLED) {
			log.info(
					"Reservation already cancelled: reservationId={}",
					event.reservationId()
			);

			return;
		}

		reservation.cancel();
		reservationRepository.save(reservation);
		inventoryOutboxService.createInventoryReleaseEvent(reservation);
		log.info(
				"Reservation cancelled and inventory released: " +
						"reservationId={}, quantity={}",
				reservation.getReservationId(),
				reservation.getQuantity()
		);
	}
}
