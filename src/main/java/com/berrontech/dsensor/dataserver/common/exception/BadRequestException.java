package com.berrontech.dsensor.dataserver.common.exception;

/**
 * Create by 郭文梁 2019/4/22 0022 15:40
 * BadRequestException
 * 请求客户端异常
 *
 * @author 郭文梁
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }

    public BadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
