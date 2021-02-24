package com.berrontech.dsensor.dataserver.common.exception;

/**
 * Create by 郭文梁 2019/5/24 0024 11:33
 * PermissionDeniedException
 * 权限被拒绝异常
 *
 * @author 郭文梁
 */
public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException() {
    }

    public PermissionDeniedException(String message) {
        super(message);
    }

    public PermissionDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionDeniedException(Throwable cause) {
        super(cause);
    }

    public PermissionDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
