package com.pvmeira.wildwest.exception;

import lombok.Getter;

@Getter
public class PackageAlreadyExistException extends ApplicationException {

    public PackageAlreadyExistException(String message) {
        super(message);
    }
}
