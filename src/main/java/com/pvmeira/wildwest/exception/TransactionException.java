package com.pvmeira.wildwest.exception;

import lombok.Getter;

@Getter
public class TransactionException extends ApplicationException {

    public TransactionException(String message) {
        super(message);
    }
}
