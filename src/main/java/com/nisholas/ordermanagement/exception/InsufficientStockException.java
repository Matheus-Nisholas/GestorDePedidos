package com.nisholas.ordermanagement.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }
}
