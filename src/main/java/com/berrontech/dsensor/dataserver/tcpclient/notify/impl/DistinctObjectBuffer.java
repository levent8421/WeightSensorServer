package com.berrontech.dsensor.dataserver.tcpclient.notify.impl;

import com.berrontech.dsensor.dataserver.common.exception.CopyException;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.common.fn.Function;
import com.berrontech.dsensor.dataserver.common.util.DeepCopyUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create By Levent at 2020/8/14 11:13
 * DistinctObjectBuffer
 * 带去重功能的对象缓存
 *
 * @author levent
 */
public class DistinctObjectBuffer<T extends Serializable> {
    private static final int INITIAL_CAPACITY = 128;
    private final Map<String, T> buffer = new ConcurrentHashMap<>(INITIAL_CAPACITY);

    /**
     * Push object into buffer
     *
     * @param obj    obj
     * @param keyFun fun for get key string
     */
    public void push(T obj, Function<T, String> keyFun) {
        final String key = keyFun.exec(obj);
        final T copy;
        try {
            copy = DeepCopyUtils.deepCopy(obj);
        } catch (CopyException e) {
            throw new InternalServerErrorException("Error on copy [" + obj + "]");
        }
        buffer.put(key, copy);
    }

    /**
     * Push object set into buffer
     *
     * @param objs   objs
     * @param keyFun function for get key string
     */
    public void push(Collection<T> objs, Function<T, String> keyFun) {
        for (T obj : objs) {
            push(obj, keyFun);
        }
    }

    /**
     * Copy Values And Clear buffer
     *
     * @return values
     */
    public List<T> copyEventAndClean() {
        if (buffer.size() <= 0) {
            return Collections.emptyList();
        }
        synchronized (buffer) {
            final List<T> objs = new ArrayList<>(buffer.values());
            buffer.clear();
            return objs;
        }
    }
}
