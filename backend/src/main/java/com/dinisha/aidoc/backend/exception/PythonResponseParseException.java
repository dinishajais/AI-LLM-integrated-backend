package com.dinisha.aidoc.backend.exception;

public class PythonResponseParseException extends RuntimeException {

    public PythonResponseParseException(String message) {
        super(message);
    }

    public PythonResponseParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
