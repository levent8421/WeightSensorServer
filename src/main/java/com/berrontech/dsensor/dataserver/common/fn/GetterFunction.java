package com.berrontech.dsensor.dataserver.common.fn;

/**
 * Create by 郭文梁 2019/4/25 0025 09:14
 * GetterFunction
 * Getter函数
 *
 * @author 郭文梁
 */
@FunctionalInterface
public interface GetterFunction<R> {
    /**
     * 执行
     *
     * @return 返回
     */
    R apply();
}
