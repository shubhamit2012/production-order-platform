package com.learning.inventoryservice.api.controller.exception;

import com.learning.inventoryservice.api.model.ErrorResponse;
import com.learning.inventoryservice.application.exception.InsufficientInventoryException;
import com.learning.inventoryservice.application.exception.InventoryNotFoundException;
import com.learning.inventoryservice.application.exception.InvalidInventoryReservationException;
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
                                .body(new ErrorResponse(
                                                "INVENTORY_NOT_FOUND",
                                                ex.getMessage()));
        }

        @ExceptionHandler(InvalidInventoryReservationException.class)
        public ResponseEntity<ErrorResponse> handleInvalidInventoryInventory(InvalidInventoryReservationException ex) {
                logger.warn("Invalid inventory exception occurred: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorResponse(
                                                "INVALID_INVENTORY",
                                                ex.getMessage()));
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
                                                "An unexpected error occurred."));
        }

}
