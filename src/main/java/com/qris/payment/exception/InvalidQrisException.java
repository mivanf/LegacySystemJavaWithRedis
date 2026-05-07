package com.qris.payment.exception;

public class InvalidQrisException extends RuntimeException {
    public InvalidQrisException(String message) {
        super(message);
    }
}
