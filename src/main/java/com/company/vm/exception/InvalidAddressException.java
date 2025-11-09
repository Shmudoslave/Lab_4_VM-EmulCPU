package com.company.vm.exception;

public class InvalidAddressException extends RuntimeException {
    public InvalidAddressException(String message) {
        super(message);
    }

    public InvalidAddressException(String message, Throwable cause) {
        super(message, cause);
    }
}