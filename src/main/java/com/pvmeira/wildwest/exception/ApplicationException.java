package com.pvmeira.wildwest.exception;

public class ApplicationException extends RuntimeException {
    private String message;


    public ApplicationException(String message) {
        super();
        this.message = message;
    }
}
