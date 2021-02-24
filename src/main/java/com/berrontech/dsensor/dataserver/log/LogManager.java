package com.berrontech.dsensor.dataserver.log;

/**
 * 日志清理接口
 *
 * @author 黄荷翔
 * 2021/2/23 15:39
 */
public interface LogManager {
    /**
     * 清除过期日志
     */
    void cleanup();
}
