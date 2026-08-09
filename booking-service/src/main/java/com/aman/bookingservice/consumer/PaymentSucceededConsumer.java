package com.aman.bookingservice.consumer;


import com.aman.bookingservice.entity.Reservation;
import com.aman.bookingservice.entity.ReservationStatus;
import com.aman.bookingservice.event.PaymentSucceededEvent;
import com.aman.bookingservice.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentSucceededConsumer {

	private static final Logger log = LoggerFactory.getLogger(PaymentSucceededConsumer.class);
	private final ReservationRepository reservationRepository;

	@KafkaListener(
			topics = "payment.succeeded",
			groupId = "booking-payment-service"
	)
	@Transactional
	public void consume(PaymentSucceededEvent event) {

		log.info(
				"Received payment.succeeded: reservationId={}, paymentId={}",
				event.reservationId(),
				event.paymentId()
		);

		Reservation reservation = reservationRepository.findByReservationId(event.reservationId())
						.orElseThrow(() -> new IllegalStateException("Reservation not found: " + event.reservationId()));

		if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
			log.info(
					"Reservation already confirmed: reservationId={}",
					event.reservationId()
			);
			return;
	}
		reservation.confirm();

		reservationRepository.save(reservation);

		log.info(
				"Reservation confirmed: reservationId={}",
				event.reservationId()
		);
	}
}
