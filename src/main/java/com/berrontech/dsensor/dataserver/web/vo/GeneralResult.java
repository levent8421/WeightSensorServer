package com.berrontech.dsensor.dataserver.web.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.springframework.http.HttpStatus.*;

/**
 * Create by 郭文梁 2019/4/15 0015 15:26
 * GeneralResult
 * 通用API返回对象
 *
 * @author 郭文梁
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralResult<T> {

    public static <T> GeneralResult<T> of(int code, String msg, T data) {
        return new GeneralResult<>(code, msg, data);
    }

    public static <T> GeneralResult<T> ok(String msg, T data) {
        return of(OK.value(), msg, data);
    }

    public static <T> GeneralResult<T> ok(T data) {
        return ok(OK.name(), data);
    }

    public static <T> GeneralResult<T> ok() {
        return ok(null);
    }

    public static <T> GeneralResult<T> permissionDenied(String msg) {
        return of(UNAUTHORIZED.value(), msg, null);
    }

    public static <T> GeneralResult<T> permissionDenied() {
        return permissionDenied(UNAUTHORIZED.name());
    }

    public static <T> GeneralResult<T> notFound(String msg) {
        return of(NOT_FOUND.value(), msg, null);
    }

    public static <T> GeneralResult<T> notFound() {
        return notFound(NOT_FOUND.name());
    }

    public static GeneralResult<?> notFound(Class<?> clazz, Integer id) {
        String msg = String.format("Could not found [%s] for id [%s]", clazz, id);
        return notFound(msg);
    }

    public static <T> GeneralResult<T> error(String msg) {
        return of(INTERNAL_SERVER_ERROR.value(), msg, null);
    }

    public static <T> GeneralResult<T> error() {
        return error(INTERNAL_SERVER_ERROR.name());
    }

    public static <T> GeneralResult<T> badRequest(String msg, T data) {
        return of(BAD_REQUEST.value(), msg, data);
    }

    public static <T> GeneralResult<T> badRequest(String msg) {
        return badRequest(msg, null);
    }

    public static <T> GeneralResult<T> badRequest() {
        return badRequest(BAD_REQUEST.name());
    }

    /**
     * 返回码 遵循HTTP返回码业务定义
     */
    private Integer code;
    /**
     * 返回信息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    public GeneralResult<T> code(int code) {
        this.code = code;
        return this;
    }

    public GeneralResult<T> msg(String msg) {
        this.msg = msg;
        return this;
    }

    public GeneralResult<T> data(T data) {
        this.data = data;
        return this;
    }
}
