package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static ru.practicum.ewm.EwmApp.FORMATTER;

@Getter
public class ApiError {

    @JsonIgnore
    private List<StackTraceElement> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;

    public ApiError(Exception e, String message) {
        this.errors = Arrays.asList(e.getStackTrace());
        this.message = message;
        choseReason(e);
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    public ApiError(Exception e, HttpStatus status) {
        this.errors = Arrays.asList(e.getStackTrace());
        this.message = stackTraceToString(e);
        choseReason(e);
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    private static final String BAD_REQUEST_REASON = "Incorrectly made request.";
    private static final String DATA_INTEGRITY_VIOLATION_REASON = "Integrity constraint has been violated.";
    private static final String NOT_FOUND_REASON = "The required object was not found.";

    private static final String ARGS_NOT_VALID_REASON = "For the requested operation the conditions are not met.";

    private static final String UNEXPECTED_REASON = "Unexpected error occurred";

    private void choseReason(Exception e) {
        String className = e.getClass().getName();
        if (className.equalsIgnoreCase(NotFoundException.class.getName())) {
            this.reason = NOT_FOUND_REASON;
            this.status = HttpStatus.NOT_FOUND.getReasonPhrase().toUpperCase();
        } else if (className.equalsIgnoreCase(NotAccessException.class.getName()) ||
                className.equalsIgnoreCase(MethodArgumentNotValidException.class.getName())) {
            this.reason = ARGS_NOT_VALID_REASON;
            this.status = HttpStatus.FORBIDDEN.getReasonPhrase().toUpperCase();
        } else if (className.equalsIgnoreCase(DataIntegrityViolationException.class.getName())) {
            this.reason = DATA_INTEGRITY_VIOLATION_REASON;
            this.status = HttpStatus.CONFLICT.getReasonPhrase().toUpperCase();
        } else if (className.equalsIgnoreCase(IllegalArgumentException.class.getName())) {
            this.reason = ARGS_NOT_VALID_REASON;
            this.status = HttpStatus.CONFLICT.getReasonPhrase().toUpperCase();
        } else if (className.equalsIgnoreCase(MissingServletRequestParameterException.class.getName()) ||
        className.equalsIgnoreCase(ConstraintViolationException.class.getName())) {
            this.reason = BAD_REQUEST_REASON;
            this.status = HttpStatus.BAD_REQUEST.getReasonPhrase().toUpperCase();
        } else {
            this.reason = UNEXPECTED_REASON;
            this.status = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase().toUpperCase();
        }
    }

    private String stackTraceToString(Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

}