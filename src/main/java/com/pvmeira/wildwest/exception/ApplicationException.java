package com.pvmeira.wildwest.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    private String message;


    public ApplicationException(String message) {
        super();
        this.message = message;
    }
}
