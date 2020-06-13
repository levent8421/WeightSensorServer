package com.berrontech.dsensor.dataserver.weight.serial;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 9:52
 * Class Name: SerialException
 * Author: Levent8421
 * Description:
 * Serial Exceptions
 *
 * @author Levent8421
 */
public class SerialException extends Exception {
    public SerialException() {
    }

    public SerialException(String message) {
        super(message);
    }

    public SerialException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerialException(Throwable cause) {
        super(cause);
    }

    public SerialException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
