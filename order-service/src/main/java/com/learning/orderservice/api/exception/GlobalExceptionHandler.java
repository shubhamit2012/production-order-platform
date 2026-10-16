package com.learning.orderservice.api.exception;

import com.learning.orderservice.api.model.ErrorResponse;
import com.learning.orderservice.application.exception.InsufficientInventoryException;
import com.learning.orderservice.application.exception.InventoryNotFoundException;
import com.learning.orderservice.application.exception.InvalidInventoryReservationException;
import com.learning.orderservice.application.exception.InventoryServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(InsufficientInventoryException.class)
        public ResponseEntity<ErrorResponse> handleInsufficientInventory(InsufficientInventoryException ex) {
                logger.warn("Insufficient inventory exception occurred: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(new ErrorResponse(
                                                "INSUFFICIENT_INVENTORY",
                                                ex.getMessage()));
        }

        @ExceptionHandler(InventoryNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleInventoryNotFound(InventoryNotFoundException ex) {
                logger.warn("Inventory not found: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(new ErrorResponse("INVENTORY_NOT_FOUND", ex.getMessage()));
        }

        @ExceptionHandler(InvalidInventoryReservationException.class)
        public ResponseEntity<ErrorResponse> handleInvalidInventory(InvalidInventoryReservationException ex) {
                logger.warn("Invalid inventory exception occurred: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorResponse(
                                                "INVALID_INVENTORY",
                                                ex.getMessage()));
        }

        @ExceptionHandler(InventoryServiceException.class)
        public ResponseEntity<ErrorResponse> handleInventoryServiceException(InventoryServiceException ex) {
                logger.warn("Inventory service operation failed", ex);
                return ResponseEntity
                                .status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body(new ErrorResponse(
                                                "INVENTORY_SERVICE_ERROR",
                                                "The order could not be processed because the inventory service is temporarily unavailable."));
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleUnreadableRequest(HttpMessageNotReadableException ex) {
                logger.warn("Request rejected: status=400, code=INVALID_REQUEST, reason=unreadable_body");
                return ResponseEntity.badRequest()
                                .body(new ErrorResponse("INVALID_REQUEST",
                                                "The request body is missing or malformed."));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
                logger.error("Unexpected exception occurred", ex);
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorResponse(
                                                "INTERNAL_SERVER_ERROR",
                                                "An unexpected error occurred. Please try again later."));
        }
}