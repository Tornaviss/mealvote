package com.mealvote.util.exception;

public class TemporaryUnavailableOperationException extends RuntimeException {

    public TemporaryUnavailableOperationException(String msg) {
        super(msg);
    }
}
