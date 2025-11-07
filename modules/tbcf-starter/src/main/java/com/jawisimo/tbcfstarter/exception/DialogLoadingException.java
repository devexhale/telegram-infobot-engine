package com.jawisimo.tbcfstarter.exception;

public class DialogLoadingException extends RuntimeException {

    public DialogLoadingException(String message) {
        super(message);
    }

    public DialogLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
