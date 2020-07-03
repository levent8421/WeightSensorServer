package com.berrontech.dsensor.dataserver.common.util;

import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/6/18 12:48
 * Class Name: NativeUtils
 * Author: Levent8421
 * Description:
 * 本地库相关工具类
 *
 * @author Levent8421
 */
public class NativeUtils {
    private static final String LIB_PATH_PROP_NAME = "java.library.path";
    private static final String PATH_ITEM_DELIMITER = ":";

    public static void addNativeLibPath(String path) {
        val pathStr = System.getProperty(LIB_PATH_PROP_NAME, "");
        val pathItems = pathStr.split(PATH_ITEM_DELIMITER);
        final List<String> pathList = new ArrayList<>(Arrays.asList(pathItems));
        if (pathList.contains(path)) {
            return;
        }
        pathList.add(path);
        val newPath = CollectionUtils.join(pathList.stream(), PATH_ITEM_DELIMITER);
        System.setProperty(LIB_PATH_PROP_NAME, newPath);
    }

    /**
     * 加载本地库
     *
     * @param path .dll .so位置
     */
    public static void loadLibrary(String path) {
        System.load(path);
    }

    /**
     * 通过库名称加载本地库
     *
     * @param name lib name
     */
    public static void loadLibraryByName(String name) {
        System.loadLibrary(name);
    }
}
