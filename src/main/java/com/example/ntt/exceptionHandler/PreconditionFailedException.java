package com.example.ntt.exceptionHandler;

public class PreconditionFailedException extends RuntimeException{

    public PreconditionFailedException(String message){
        super(message);
    }
}
