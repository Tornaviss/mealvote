package com.mealvote.util.exception;

public class NotFoundException extends RuntimeException {

    private static final String MESSAGE_ADDITION = " is not found";

    public NotFoundException(String message) {
        super(message + MESSAGE_ADDITION);
    }
}
