package com.example.ntt.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(ResourceNotFoundException ex){
        return new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "Resource not found");
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorMessage unauthorizedException(UnauthorizedException ex){
        return new ErrorMessage(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                "Action denied");
    }

    @ExceptionHandler(value = PreconditionFailedException.class)
    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    public ErrorMessage preconditionFailedException(PreconditionFailedException ex){
        return new ErrorMessage(
                HttpStatus.PRECONDITION_FAILED.value(),
                ex.getMessage(),
                "Precondition Failed");
    }
}