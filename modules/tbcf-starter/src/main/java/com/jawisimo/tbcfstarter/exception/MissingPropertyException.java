package com.jawisimo.tbcfstarter.exception;

public class MissingPropertyException extends RuntimeException {

    public MissingPropertyException(String propertyName) {
        super("Required property is missing: " + propertyName);
    }
}
