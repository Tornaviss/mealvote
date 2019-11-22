package com.mealvote.util.exception;

public class ExceptionInfo {
    private final String url;
    private final String exceptionSimpleName;
    private final String[] details;

    public ExceptionInfo(String exceptionSimpleName, CharSequence url, String... details) {
        this.url = url.toString();
        this.exceptionSimpleName = exceptionSimpleName;
        this.details = details;
    }
}