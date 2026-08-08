package com.aman.bookingservice.exception;

public class InsufficientInventoryException extends RuntimeException {
	public InsufficientInventoryException(String message) {
		super(message);
	}
}
