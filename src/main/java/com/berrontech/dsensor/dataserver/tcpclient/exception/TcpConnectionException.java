package com.berrontech.dsensor.dataserver.tcpclient.exception;

import lombok.NoArgsConstructor;

/**
 * Create By Levent8421
 * Create Time: 2020/6/11 17:05
 * Class Name: TcpConnectionException
 * Author: Levent8421
 * Description:
 * TCP Connection Exception
 *
 * @author Levent8421
 */
@NoArgsConstructor
public class TcpConnectionException extends ApiException {
    public TcpConnectionException(String msg) {
        super(msg);
    }

    public TcpConnectionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
