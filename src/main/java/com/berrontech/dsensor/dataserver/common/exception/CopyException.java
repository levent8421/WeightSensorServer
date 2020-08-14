package com.berrontech.dsensor.dataserver.common.exception;

/**
 * Create By Levent at 2020/8/14 10:55
 * CopyException
 * CopyException
 *
 * @author levent
 */
public class CopyException extends Exception {
    public CopyException() {
    }

    public CopyException(String message) {
        super(message);
    }

    public CopyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CopyException(Throwable cause) {
        super(cause);
    }

    public CopyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
