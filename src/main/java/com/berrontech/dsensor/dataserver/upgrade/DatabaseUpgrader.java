package com.berrontech.dsensor.dataserver.upgrade;

import org.springframework.core.io.support.EncodedResource;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/7/7 19:37
 * Class Name: DatabaseUpgrader
 * Author: Levent8421
 * Description:
 * 数据库更新组件
 *
 * @author Levent8421
 */
public interface DatabaseUpgrader {
    /**
     * 检查数据库更新
     */
    void checkForUpgrade();

    /**
     * 初始化数据库
     */
    void checkForInit();

    /**
     * 执行数据库更新脚本
     *
     * @param resources sql script resource
     * @throws Exception e
     */
    void runUpgradeScript(List<EncodedResource> resources) throws Exception;

    /**
     * 重置数据库
     */
    void resetDatabase();
}
