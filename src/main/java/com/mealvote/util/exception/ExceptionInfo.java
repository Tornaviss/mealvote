package com.mealvote.util.exception;

public class ExceptionInfo {
    private final String url;
    private final String[] details;

    public ExceptionInfo(CharSequence url, String... details) {
        this.url = url.toString();
        this.details = details;
    }
}