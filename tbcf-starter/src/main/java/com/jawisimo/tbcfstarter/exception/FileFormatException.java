package com.jawisimo.tbcfstarter.exception;

public class FileFormatException extends RuntimeException {

    public FileFormatException(String fileName)  {
        super("Unsupported file format: " + fileName);
    }
}
