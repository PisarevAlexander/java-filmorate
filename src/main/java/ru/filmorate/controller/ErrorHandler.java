package ru.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.filmorate.exception.BadRequestException;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.model.ErrorResponse;

/**
 * Error handler
 */

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    /**
     * Handle incorrect parameter exception error response.
     * @param e the exception
     * @return the error response
     */

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(final BadRequestException e) {
        log.warn("Ошибка с полем {}", e.getParameter());
        return new ErrorResponse(
                String.format("Ошибка с полем \"%s\".", e.getParameter())
        );
    }

    /**
     * Handle not found exception error response.
     * @param e the exception
     * @return the error response
     */

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Handle throwable error response.
     * @param e the exception
     * @return the error response
     */

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn("Произошла непредвиденная ошибка.");
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}