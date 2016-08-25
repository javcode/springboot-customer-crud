package com.javcode.springboot.exception;

public class JsonException {

    private final String message;

    public JsonException(Throwable t) {
        this.message = t.getMessage();
    }

    public JsonException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
