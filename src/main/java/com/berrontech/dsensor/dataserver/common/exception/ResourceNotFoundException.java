package com.berrontech.dsensor.dataserver.common.exception;

import java.lang.reflect.Type;

/**
 * Create by 郭文梁 2019/4/17 0017 11:56
 * ResourceNotFoundException
 * 资源找不到异常
 *
 * @author 郭文梁
 * @data 2019/4/17 0017
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String msg) {
        super(msg);
    }

    public ResourceNotFoundException(Type clazz) {
        this(String.format("The resource [%s] could not be found!", clazz.getTypeName()));
    }

    public ResourceNotFoundException(Type clazz, Object id) {
        this(String.format("The resource [%s] for id [%s] could not be found!", clazz.getTypeName(), id));
    }
}
