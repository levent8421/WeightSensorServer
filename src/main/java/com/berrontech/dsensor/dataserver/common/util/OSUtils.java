package com.berrontech.dsensor.dataserver.common.util;

/**
 * Create By Levent8421
 * Create Time: 2020/7/25 13:38
 * Class Name: OSUtils
 * Author: Levent8421
 * Description:
 * 操作系统相关工具类
 *
 * @author Levent8421
 */
public class OSUtils {
    private static final String OS_NAME_PROP_NAME = "os.name";
    private static final String OS_ARCH_PROP_NAME = "os.arch";

    /**
     * Get OS Name
     *
     * @return os name
     */
    public static String getOsName() {
        return System.getProperty(OS_NAME_PROP_NAME);
    }

    /**
     * GET OS ARCH
     *
     * @return string arch info
     */
    public static String getArch() {
        return System.getProperty(OS_ARCH_PROP_NAME);
    }

    /**
     * 当前操作系统是否为Windows
     *
     * @return bool
     */
    public static boolean isWindows() {
        String osName = getOsName();
        if (osName == null) {
            return false;
        }
        osName = osName.toLowerCase();
        return osName.contains("windows");
    }
}
