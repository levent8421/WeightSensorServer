package com.berrontech.dsensor.dataserver.common.util;

import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;

/**
 * Create By Levent8421
 * Create Time: 2020/7/6 10:48
 * Class Name: VersionUtils
 * Author: Levent8421
 * Description:
 * 版本相关工具
 *
 * @author Levent8421
 */
@Slf4j
public class VersionUtils {
    private static final String SUB_VERSION_DELIMITER = ".";

    /**
     * 比较两个版本号
     * 如果versionA 大于 versionB 返回 一个正整数
     * 如果versionA 小于 versionB 返回一个负整数
     * 如果versionA 等于 versionB 返回0
     *
     * @param versionA versionA
     * @param versionB versionB
     * @return int
     */
    public static int compare(String versionA, String versionB) {
        if (versionA == null || versionB == null) {
            throw new NullPointerException("Illegal versionName");
        }
        final String[] versionsA = versionA.split(SUB_VERSION_DELIMITER);
        final String[] versionsB = versionB.split(SUB_VERSION_DELIMITER);
        if (versionsA.length != versionsB.length) {
            throw new IllegalArgumentException("Version name format mismatch, "
                    + versionA + " and " + versionB);
        }
        for (int i = 0; i < versionsA.length; i++) {
            final String subVersionA, subVersionB;
            subVersionA = versionsA[i];
            subVersionB = versionsB[i];
            try {
                final int subVersionNumA = Integer.parseInt(subVersionA);
                final int subVersionNumB = Integer.parseInt(subVersionB);
                if (subVersionNumA != subVersionNumB) {
                    return subVersionNumA - subVersionNumB;
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalidate versionName [" + versionA + "/" + versionB + "]!");
            }
        }
        return 0;
    }

    /**
     * 比较两个数据库版本
     *
     * @param versionA va
     * @param versionB vb
     * @return result
     */
    public static int compareDatabaseVersion(String versionA, String versionB) {
        final int va, vb;
        try {
            va = NumberUtils.parseInt(versionA, 1);
            vb = NumberUtils.parseInt(versionB, 1);
        } catch (NumberFormatException e) {
            throw new InternalServerErrorException("Invalidate database version!", e);
        }
        return va - vb;
    }
}
