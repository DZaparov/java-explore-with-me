package ru.practicum.ewm.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.info("{} {}", HttpStatus.NOT_FOUND, e.getMessage());
        return ApiError.builder()
                .errors(e.toString())
                .message(e.getMessage())
                .reason("The required object was not found.")
                .status(HttpStatus.NOT_FOUND.toString())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT) //409
    public ApiError handleConflictException(final ConflictException e) {
        log.info("{} {}", HttpStatus.CONFLICT, e.getMessage());
        return ApiError.builder()
                .errors(e.toString())
                .message(e.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .status(HttpStatus.CONFLICT.toString())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public ApiError handleValidationException(final Exception e) {
        log.info("{} {}", HttpStatus.BAD_REQUEST, e.getMessage());
        return ApiError.builder()
                .errors(e.toString())
                .message(e.getMessage())
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public ApiError handleBadRequestException(final BadRequestException e) {
        log.info("{} {}", HttpStatus.BAD_REQUEST, e.getMessage());
        return ApiError.builder()
                .errors(e.toString())
                .message(e.getMessage())
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //500
    public ApiError handleThrowable(final Throwable e) {
        log.info("{} {}", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        return ApiError.builder()
                .errors(e.toString())
                .message(e.getMessage())
                .reason("The server encountered an unexpected condition.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}
