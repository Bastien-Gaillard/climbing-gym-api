package fr.climbinggym.adapter.in.web;

import fr.climbinggym.domain.shared.BusinessRuleException;
import fr.climbinggym.domain.shared.InvalidValueException;
import fr.climbinggym.domain.shared.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError.Body notFound(NotFoundException e) {
        return ApiError.of(e.code(), e.getMessage());
    }

    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError.Body businessRule(BusinessRuleException e) {
        return ApiError.of(e.code(), e.getMessage());
    }

    @ExceptionHandler(InvalidValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError.Body invalidValue(InvalidValueException e) {
        var fields = new LinkedHashMap<String, String>();
        fields.put(e.field(), e.getMessage());
        return ApiError.of(e.code(), e.getMessage(), fields);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError.Body validation(MethodArgumentNotValidException e) {
        var fields = new LinkedHashMap<String, String>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return ApiError.of("VALIDATION_ERROR", "Request body is invalid", fields);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError.Body badRequest(Exception e) {
        return ApiError.of("BAD_REQUEST", "Malformed request");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError.Body unexpected(Exception e) {
        log.error("Unexpected error", e);
        return ApiError.of("INTERNAL_ERROR", "Unexpected error");
    }
}
