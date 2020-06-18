package com.berrontech.dsensor.dataserver.common.util;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Create by 郭文梁 2019/7/1 0001 08:57
 * CollectionUtils
 * 容器相关操作工具类
 *
 * @author 郭文梁
 * @data 2019/7/1 0001
 */
public class CollectionUtils {
    /**
     * 将容器内容以delimiter分隔拼接成字符串
     *
     * @param source    容器的stream
     * @param delimiter 分隔符
     * @return 字符串
     */
    public static String join(Stream<?> source, String delimiter) {
        return source.map(Object::toString).reduce((v1, v2) -> v1 + delimiter + v2).orElse("");
    }

    /**
     * 检查集合是否为空
     *
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    /**
     * 检查Map是否为空
     *
     * @param map map
     * @return 是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    /**
     * 获取集合大小 null安全
     *
     * @param collection 集合
     * @return 大小
     */
    public static int size(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    /**
     * 获取Map大小 null安全
     *
     * @param map map
     * @return 大小
     */
    public static int size(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }
}
