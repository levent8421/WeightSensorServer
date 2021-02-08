package com.berrontech.dsensor.dataserver.weight;

/**
 * Create By Levent8421
 * Create Time: 2021/2/7 18:18
 * Class Name: TareException
 * Author: Levent8421
 * Description:
 * 去皮错误
 *
 * @author Levent8421
 */
public class TareException extends Exception {
    public TareException() {
    }

    public TareException(String message) {
        super(message);
    }

    public TareException(String message, Throwable cause) {
        super(message, cause);
    }

    public TareException(Throwable cause) {
        super(cause);
    }

    public TareException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
