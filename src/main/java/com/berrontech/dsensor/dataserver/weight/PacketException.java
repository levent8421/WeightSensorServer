package com.berrontech.dsensor.dataserver.weight;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 19:26
 * Class Name: PacketException
 * Author: Levent8421
 * Description:
 * 数据包异常
 *
 * @author Levent8421
 */
public class PacketException extends Exception {
    public PacketException() {
    }

    public PacketException(String message) {
        super(message);
    }

    public PacketException(String message, Throwable cause) {
        super(message, cause);
    }

    public PacketException(Throwable cause) {
        super(cause);
    }

    public PacketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
