package com.zerori.dev.zerori_image;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private static final String RES_MSG_FIELD_NAME = "message";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleException(final MethodArgumentNotValidException e) {
        final String message = e.getFieldError() != null ? e.getFieldError().getDefaultMessage() + " (" + e.getFieldError().getField() + ")"
                : e.getGlobalError() != null ? e.getGlobalError().getDefaultMessage() : e.getMessage();
        // TODO slack
        return Map.of(RES_MSG_FIELD_NAME, message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleException(final MissingServletRequestParameterException e) {
        // TODO slack
        return Map.of(RES_MSG_FIELD_NAME, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleException(final ConstraintViolationException e) {
        // TODO slack
        return Map.of(RES_MSG_FIELD_NAME, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleException(final MethodArgumentTypeMismatchException e) {
        final StringBuilder errMessageBuilder = new StringBuilder(e.getName()).append(" is ").append(e.getErrorCode());
        if (e.getValue() != null) {
            errMessageBuilder.append(" (").append(e.getValue()).append(")");
        }
        final String message = new String(errMessageBuilder);

        // TODO slack
        return Map.of(RES_MSG_FIELD_NAME, message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleException(final BindException e) {
        final String message = e.getFieldError() != null ? e.getFieldError().getDefaultMessage() + " (" + e.getFieldError().getField() + ")"
                : e.getGlobalError() != null ? e.getGlobalError().getDefaultMessage() : e.getMessage();
        // TODO slack
        return Map.of(RES_MSG_FIELD_NAME, message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleException(final HttpMessageNotReadableException e) {
        // TODO slack
        return Map.of(RES_MSG_FIELD_NAME, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleException(final NoHandlerFoundException e) {
        // TODO slack "Not Found URL"
        return Map.of(RES_MSG_FIELD_NAME, e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrResException.ErrorResponseBody> handleException(final ErrResException e) {
        if (e.getHttpStatus().is5xxServerError()) {
            // TODO slack
        }
        return new ResponseEntity<>(e.getErrorResponseBody(), e.getHttpStatus());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleException(final Exception e) {
        return Map.of(RES_MSG_FIELD_NAME, e.getMessage());
    }
}
