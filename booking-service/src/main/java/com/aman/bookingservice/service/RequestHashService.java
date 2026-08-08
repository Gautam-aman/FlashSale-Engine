package com.aman.bookingservice.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.aman.bookingservice.dto.CreateReservationRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestHashService {

	public String hash(CreateReservationRequest request){

		String value = request.userId() + "|" + request.ticketTypeId() + "|" + request.quantity();

		try{

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
			StringBuilder result = new StringBuilder();
			for (byte b : hash) {
				result.append(String.format("%02x", b));
			}
			return result.toString();
		}
		catch (NoSuchAlgorithmException e){
			throw new IllegalStateException(
					"SHA-256 algorithm not available",
					e
			);
		}

	}

}
