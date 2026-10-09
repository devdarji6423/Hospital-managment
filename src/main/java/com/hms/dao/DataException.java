package com.hms.dao;

/** Unchecked wrapper for database failures, so the UI can show one clear error message. */
public class DataException extends RuntimeException {
    public DataException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataException(String message) {
        super(message);
    }
}
