package ru.practicum.ewm.exception;

public class RequestDateException extends RuntimeException {
    public RequestDateException(final String message) {
        super(message);
    }
}