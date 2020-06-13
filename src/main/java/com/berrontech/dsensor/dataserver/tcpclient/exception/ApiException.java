package com.berrontech.dsensor.dataserver.tcpclient.exception;

import lombok.NoArgsConstructor;

/**
 * Create By Levent8421
 * Create Time: 2020/6/10 17:22
 * Class Name: ApiException
 * Author: Levent8421
 * Description:
 * Api Exception
 *
 * @author Levent8421
 */
@NoArgsConstructor
public class ApiException extends Exception {
    public ApiException(String msg) {
        super(msg);
    }

    public ApiException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
