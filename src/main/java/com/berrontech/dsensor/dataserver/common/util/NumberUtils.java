package com.berrontech.dsensor.dataserver.common.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Create By Levent8421
 * Create Time: 2020/7/30 16:28
 * Class Name: NumberUtils
 * Author: Levent8421
 * Description:
 * 数字相关工具类
 *
 * @author Levent8421
 */
@Slf4j
public class NumberUtils {
    public static int parseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            log.warn("Unable to parse [{}] to int, use default value [{}]!", str, defaultValue);
            return defaultValue;
        }
    }
}
