package com.spotlight.platform.userprofile.api.core.exceptions;

public class InvalidPropertyException extends RuntimeException {
    public InvalidPropertyException(Exception exception) {
        super(exception);
    }
}
