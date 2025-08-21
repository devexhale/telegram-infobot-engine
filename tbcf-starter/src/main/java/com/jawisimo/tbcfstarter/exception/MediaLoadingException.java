package com.jawisimo.tbcfstarter.exception;

public class MediaLoadingException extends RuntimeException {
    public MediaLoadingException(String message) {
        super(message);
    }

    public MediaLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
