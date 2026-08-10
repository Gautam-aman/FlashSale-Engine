package com.aman.paymentservice.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.aman.paymentservice.entity.OutboxEvent;
import com.aman.paymentservice.entity.Payment;
import com.aman.paymentservice.event.PaymentFailedEvent;
import com.aman.paymentservice.event.PaymentSucceededEvent;
import com.aman.paymentservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentOutboxService {

	private final OutboxEventRepository outboxEventRepository;
	private final ObjectMapper objectMapper;

	public void createPaymentSucceededEvent(Payment payment) {

		PaymentSucceededEvent event = new PaymentSucceededEvent(
						UUID.randomUUID(),
						payment.getPaymentId(),
						payment.getReservationId(),
						payment.getUserId(),
						LocalDateTime.now()
				);

		saveEvent(
				event.eventId(),
				"PAYMENT_SUCCEEDED",
				payment.getReservationId().toString(),
				event
		);
	}

	public void createPaymentFailedEvent(Payment payment) {

		PaymentFailedEvent event =
				new PaymentFailedEvent(
						UUID.randomUUID(),
						payment.getPaymentId(),
						payment.getReservationId(),
						payment.getUserId(),
						"Payment processing failed",
						LocalDateTime.now()
				);

		saveEvent(
				event.eventId(),
				"PAYMENT_FAILED",
				payment.getReservationId().toString(),
				event
		);
	}

	private void saveEvent(UUID eventId, String eventType, String aggregateId, Object event) {
		try {
			String payload = objectMapper.writeValueAsString(event);

			OutboxEvent outboxEvent =
					new OutboxEvent(
							eventId,
							eventType,
							aggregateId,
							payload
					);

			outboxEventRepository.save(outboxEvent);
		} catch (Exception exception) {
			throw new IllegalStateException(
					"Failed to serialize payment event", exception
			);
		}
	}

}
