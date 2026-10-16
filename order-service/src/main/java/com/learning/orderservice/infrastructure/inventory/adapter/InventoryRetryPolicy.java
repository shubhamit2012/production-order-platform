package com.learning.orderservice.infrastructure.inventory.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@Component
public class InventoryRetryPolicy {

    private static final Logger logger = LoggerFactory.getLogger(InventoryRetryPolicy.class);

    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_BACKOFF_MS = 2000;

    public <T> T execute(Supplier<T> operation) {

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                logger.debug("Executing inventory operation: attempt={}, maxAttempts={}", attempt, MAX_ATTEMPTS);
                return operation.get();
            } catch (RuntimeException ex) {
                if (attempt == MAX_ATTEMPTS || !isRetryable(ex)) {
                    logger.debug("Inventory operation stopped: attempt={}, maxAttempts={}, failureType={}",
                        attempt, MAX_ATTEMPTS, ex.getClass().getSimpleName());
                    throw ex;
                }
                logger.warn("Retrying inventory operation: nextAttempt={}, maxAttempts={}, backoffMs={}, failureType={}",
                    attempt + 1, MAX_ATTEMPTS, INITIAL_BACKOFF_MS, ex.getClass().getSimpleName());
                waitBeforeRetry(attempt);
            }
        }
        throw new IllegalStateException("Unreachable code");
    }

    private boolean isRetryable(RuntimeException ex) {
        if (ex instanceof RestClientResponseException responseException) {
            int status = responseException.getStatusCode().value();
            logger.debug("Evaluating inventory retry: status={}", status);
            return status == 500
                    || status == 502
                    || status == 503
                    || status == 504;
        }

        if (ex instanceof ResourceAccessException) {
            return !isTimeout(ex);
        }
        return false;
    }

    private boolean isTimeout(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SocketTimeoutException
                    || current instanceof HttpTimeoutException
                    || current instanceof TimeoutException) {
                
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private void waitBeforeRetry(int attempt) {
        //Use it for exponential backoff
        // long delayMs = INITIAL_BACKOFF_MS * (1L << (attempt - 1));

        //Use fixed backoff for retries
        long delayMs = INITIAL_BACKOFF_MS;
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            logger.warn("Inventory retry interrupted: attempt={}", attempt);
            throw new IllegalStateException(
                    "Interrupted while waiting to retry inventory request",
                    interruptedException
            );
        }
    }
}