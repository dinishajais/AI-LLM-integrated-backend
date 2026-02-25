package com.dinisha.aidoc.backend.exception;

public class PythonResponseParseException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PythonResponseParseException(String message) {
        super(message);
    }

    public PythonResponseParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
