package ru.practicum.ewm.exception;

public class NotAccessException extends RuntimeException {
    public NotAccessException() {
    }

    public NotAccessException(String message) {
        super(message);
    }
}
