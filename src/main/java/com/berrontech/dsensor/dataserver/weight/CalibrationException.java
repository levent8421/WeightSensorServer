package com.berrontech.dsensor.dataserver.weight;

/**
 * Create By Levent8421
 * Create Time: 2020/11/4 17:31
 * Class Name: CalibrationException
 * Author: Levent8421
 * Description:
 * 标定错误
 *
 * @author Levent8421
 */
public class CalibrationException extends Exception {
    public CalibrationException() {
    }

    public CalibrationException(String message) {
        super(message);
    }

    public CalibrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CalibrationException(Throwable cause) {
        super(cause);
    }

    public CalibrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
