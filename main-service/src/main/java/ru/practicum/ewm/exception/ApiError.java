package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static ru.practicum.ewm.EwmApp.FORMATTER;

public class ApiError {

    private List<StackTraceElement> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;

    public ApiError(Exception e, HttpStatus status) {
        this.errors = Arrays.asList(e.getStackTrace());
        this.message = e.getMessage();
        this.reason = status.getReasonPhrase();
        this.status = status.toString();
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    /*public ApiError(Exception e, HttpStatus status) {
        this.errors = Arrays.asList(e.getStackTrace());
        this.message = e.getMessage();
        this.reason = status.getReasonPhrase();
        this.status = status.toString();
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }*/



    /*private static final String BAD_REQUEST_REASON = "Incorrectly made request.";
    private static final String CONFLICT_REASON = "Integrity constraint has been violated."; //отличается(3 кейса)
    private static final String NOT_FOUND_REASON = "The required object was not found.";


    private final HttpStatus status;
    private final String reason;
    private final String message;
    private final String timestamp;

    public ApiError(Throwable e, HttpStatus status) {
        this.status = status;
        this.reason = e.getCause().toString();
        this.message = stackTraceToString(e);
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String stackTraceToString(Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        String exceptionAsString = writer.toString();
        return exceptionAsString;
    }

    private String getReason(HttpStatus status) {
        switch (status) {
            case BAD_REQUEST:
                return BAD_REQUEST_REASON;
            case NOT_FOUND:
                return NOT_FOUND_REASON;
            case CONFLICT:
                return CONFLICT_REASON;
            default:
                return "Unexpected error";
        }
    }*/
}