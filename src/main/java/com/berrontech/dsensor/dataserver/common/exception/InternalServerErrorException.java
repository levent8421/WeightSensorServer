package com.berrontech.dsensor.dataserver.common.exception;

/**
 * Create by 郭文梁 2019/4/17 0017 13:36
 * InternalServerErrorException
 * 服务器异常
 *
 * @author 郭文梁
 * @data 2019/4/17 0017
 */
public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException() {
    }

    public InternalServerErrorException(String message) {
        super(message);
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerErrorException(Throwable cause) {
        super(cause);
    }

    public InternalServerErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
