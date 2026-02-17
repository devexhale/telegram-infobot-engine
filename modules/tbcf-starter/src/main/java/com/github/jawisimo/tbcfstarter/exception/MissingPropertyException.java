package com.github.jawisimo.tbcfstarter.exception;

public class MissingPropertyException extends RuntimeException {

  public MissingPropertyException(String propertyName) {
    super("Required properties are missing: " + propertyName);
  }
}
