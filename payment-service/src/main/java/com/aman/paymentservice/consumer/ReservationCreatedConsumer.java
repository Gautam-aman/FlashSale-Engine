package com.aman.paymentservice.consumer;

import com.aman.paymentservice.entity.Payment;
import com.aman.paymentservice.entity.PaymentStatus;
import com.aman.paymentservice.event.ReservationCreatedEvent;
import com.aman.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationCreatedConsumer {

	private final PaymentRepository paymentRepository;
	private final PaymentEventPublisher paymentEventPublisher;

	private static final Logger log = LoggerFactory.getLogger(ReservationCreatedConsumer.class);

	@KafkaListener(
			topics = "reservation.created",
			groupId = "payment-service"
	)
	@Transactional
	public void consume(ReservationCreatedEvent event){

		log.info("Received reservation.created: reservationId={}", event.reservationId());
		if (paymentRepository.findByReservationId(event.reservationId()).isPresent()) {
			log.info(
					"Payment already exists for reservationId={}",
					event.reservationId()
			);
			return;
		}

		Payment payment = new Payment(
						event.reservationId(),
						event.userId(),
						event.amount()
				);
		paymentRepository.save(payment);
		processPayment(payment);
		paymentRepository.save(payment);

		if (payment.getStatus() == PaymentStatus.SUCCESS) {
			paymentEventPublisher.publishSuccess(payment);
		} else {
			paymentEventPublisher.publishFailure(payment);
		}

	}

	private void processPayment(Payment payment) {
		payment.markSuccess();
	}

}
