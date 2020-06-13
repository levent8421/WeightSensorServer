package com.berrontech.dsensor.dataserver.tcpclient.exception;

/**
 * Create By Levent8421
 * Create Time: 2020/6/11 17:06
 * Class Name: MessageException
 * Author: Levent8421
 * Description:
 * Message Exception
 *
 * @author Levent8421
 */
public class MessageException extends ApiException {
    public MessageException() {
    }

    public MessageException(String msg) {
        super(msg);
    }

    public MessageException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
