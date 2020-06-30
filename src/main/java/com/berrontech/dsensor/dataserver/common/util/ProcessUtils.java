package com.berrontech.dsensor.dataserver.common.util;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.lang.management.ManagementFactory;

/**
 * Create By Levent8421
 * Create Time: 2020/6/30 11:26
 * Class Name: ProcessUtils
 * Author: Levent8421
 * Description:
 * 进程相关工具类
 *
 * @author Levent8421
 */
@Slf4j
public class ProcessUtils {
    /**
     * 获取当前进程ID
     *
     * @return pid
     */
    public static int getProcessId() {
        val runtimeName = ManagementFactory.getRuntimeMXBean().getName();
        val pidPair = runtimeName.split("@");
        if (pidPair.length <= 0) {
            return -1;
        }
        try {
            return Integer.parseInt(pidPair[0]);
        } catch (Exception e) {
            log.warn("Invalidate pid {}", pidPair[0]);
            return -1;
        }
    }
}
