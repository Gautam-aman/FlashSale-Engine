package com.aman.bookingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InsufficientInventoryException.class)
	public ResponseEntity<String> handleInsufficientInventory(InsufficientInventoryException exception) {
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(exception.getMessage());
	}

}
