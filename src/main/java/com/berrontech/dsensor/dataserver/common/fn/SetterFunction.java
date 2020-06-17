package com.berrontech.dsensor.dataserver.common.fn;

/**
 * Create by 郭文梁 2019/4/25 0025 09:16
 * SetterFunction
 * Setter函数
 *
 * @author 郭文梁
 * @data 2019/4/25 0025
 */
@FunctionalInterface
public interface SetterFunction<P> {
    /**
     * 执行
     *
     * @param p 参数
     */
    void apply(P p);
}
