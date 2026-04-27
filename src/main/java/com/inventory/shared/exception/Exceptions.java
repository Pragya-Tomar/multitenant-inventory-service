package com.inventory.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public final class Exceptions {

    private Exceptions() {}

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class TenantAccessDeniedException extends RuntimeException {
        public TenantAccessDeniedException() {
            super("Access denied: resource belongs to a different tenant");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidRequestException extends RuntimeException {
        public InvalidRequestException(String message) {
            super(message);
        }
    }
}
