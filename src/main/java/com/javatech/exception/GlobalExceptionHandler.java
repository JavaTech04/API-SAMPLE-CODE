package com.javatech.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Date;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleValidException(Exception ex, WebRequest request) {
        log.info("=================Handle_Exception_Running=================");
        ExceptionResponse response = new ExceptionResponse();
        response.setTimestamp(new Date());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setPath(request.getDescription(false).replace("uri=", ""));

        String message = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException) {
            int start = message.lastIndexOf("[");
            int end = message.lastIndexOf("]");
            message = message.substring(start + 1, end - 1);
            response.setError("Payload invalid");
        } else if (ex instanceof ConstraintViolationException) {
            message = message.substring(message.indexOf(" ") + 1);
            response.setError("PathVariable invalid");
        }
        response.setMessage(message);
        return response;
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ResourceNotFoundException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleInternalServerException(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse();
        response.setTimestamp(new Date());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setPath(request.getDescription(false).replace("uri=", ""));

        if (ex instanceof MethodArgumentTypeMismatchException) {
            response.setError("Payload invalid");
            response.setMessage("Failed to convert value of type");
        } else if (ex instanceof ResourceNotFoundException) {
            response.setError("Internal Server Error");
            response.setMessage("Something went wrong");
        } else if (ex instanceof IllegalArgumentException) {
            response.setError("Payload invalid");
            response.setMessage("Property must not be null or empty");

        }
        return response;
    }
}
