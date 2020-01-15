package com.mealvote.util.exception;

public class NotFoundException extends RuntimeException {

    private static final String MESSAGE_ADDITION = " was not found";

    public NotFoundException(String message) {
        super(message + MESSAGE_ADDITION);
    }

    public NotFoundException(String message, Throwable cause) {
        this(message);
        initCause(cause);
    }
}
