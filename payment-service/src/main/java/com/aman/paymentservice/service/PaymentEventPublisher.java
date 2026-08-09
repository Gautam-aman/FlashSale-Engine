package com.aman.paymentservice.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.aman.paymentservice.entity.Payment;
import com.aman.paymentservice.event.PaymentFailedEvent;
import com.aman.paymentservice.event.PaymentSucceededEvent;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventPublisher {
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void publishSuccess(Payment payment) {
		PaymentSucceededEvent event =
				new PaymentSucceededEvent(
						UUID.randomUUID(),
						payment.getPaymentId(),
						payment.getReservationId(),
						payment.getUserId(),
						LocalDateTime.now()
				);

		kafkaTemplate.send(
				"payment.succeeded",
				payment.getReservationId().toString(),
				event
		);

	}

	public void publishFailure(Payment payment) {

		PaymentFailedEvent event = new PaymentFailedEvent(
						UUID.randomUUID(),
						payment.getPaymentId(),
						payment.getReservationId(),
						payment.getUserId(),
						"Payment processing failed",
						LocalDateTime.now()
				);

		kafkaTemplate.send("payment.failed",
				payment.getReservationId().toString(),
				event
		);
	}

}
