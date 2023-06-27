package com.spotlight.platform.userprofile.api.core.exceptions;

public class InvalidUpdateCommandException extends RuntimeException {
    public InvalidUpdateCommandException(Exception exception) {
        super(exception);
    }
}
