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
    public static final String OS_NAME_WINDOWS = "windows";
    private static final String LIB_EXT_NAME_WINDOWS = ".dll";
    private static final String LIB_EXT_NAME_LINUX = ".so";
    private static final String OS_NAME_PROP_NAME = "os.name";
    private static final String OS_ARCH_PROP_NAME = "os.arch";
    private static final String OS_NAME;
    private static final String OS_ARCH;
    private static final boolean WINDOW_MARK;

    static {
        OS_NAME = System.getProperty(OS_NAME_PROP_NAME);
        OS_ARCH = System.getProperty(OS_ARCH_PROP_NAME);
        WINDOW_MARK = OS_NAME != null && OS_NAME.toLowerCase().contains("windows");
    }

    /**
     * Get OS Name
     *
     * @return os name
     */
    public static String getOsName() {
        return OS_NAME;
    }

    /**
     * GET OS ARCH
     *
     * @return string arch info
     */
    public static String getArch() {
        return OS_ARCH;
    }

    /**
     * 当前操作系统是否为Windows
     *
     * @return bool
     */
    public static boolean isWindows() {
        return WINDOW_MARK;
    }

    public static String getLibExtName() {
        return isWindows() ? LIB_EXT_NAME_WINDOWS : LIB_EXT_NAME_LINUX;
    }
}
