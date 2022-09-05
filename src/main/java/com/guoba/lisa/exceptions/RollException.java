package com.guoba.lisa.exceptions;

public class RollException extends Exception {
    public RollException() {
    }

    public RollException(String message) {
        super(message);
    }

    public RollException(String message, Throwable cause) {
        super(message, cause);
    }

    public RollException(Throwable cause) {
        super(cause);
    }

    public RollException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
