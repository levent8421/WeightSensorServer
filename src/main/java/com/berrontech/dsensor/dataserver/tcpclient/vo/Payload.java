package com.berrontech.dsensor.dataserver.tcpclient.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create By Levent8421
 * Create Time: 2020/6/10 16:48
 * Class Name: Payload
 * Author: Levent8421
 * Description:
 * Simple Payload
 *
 * @author Levent8421
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payload<T> {
    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;
    public static final int ERROR = 500;
    public static final String OK_MSG = "OK";
    public static final String BAD_REQUEST_MSG = "BAD REQUEST";
    public static final String ERROR_MSG = "ERROR";

    public static <T> Payload<T> of(int code, String msg, T data) {
        return new Payload<>(code, msg, data);
    }

    public static <T> Payload<T> ok(String msg, T data) {
        return of(OK, msg, data);
    }

    public static <T> Payload<T> ok(T data) {
        return ok(OK_MSG, data);
    }

    public static <T> Payload<T> ok() {
        return ok(OK_MSG, null);
    }

    public static <T> Payload<T> badRequest(String msg, T data) {
        return of(BAD_REQUEST, msg, data);
    }

    public static <T> Payload<T> badRequest(T data) {
        return badRequest(BAD_REQUEST_MSG, data);
    }

    public static <T> Payload<T> badRequest() {
        return badRequest(null);
    }

    public static <T> Payload<T> error(String msg, T data) {
        return of(ERROR, msg, data);
    }

    public static <T> Payload<T> error(T data) {
        return error(ERROR_MSG, data);
    }

    public static <T> Payload<T> error() {
        return badRequest(null);
    }

    /**
     * Status Code
     */
    private int code;
    /**
     * Message
     */
    private String msg;
    /**
     * Data
     */
    private T data;
}