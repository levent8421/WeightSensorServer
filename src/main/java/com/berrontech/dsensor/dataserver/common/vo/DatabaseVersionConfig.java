package com.berrontech.dsensor.dataserver.common.vo;

import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/7/4 18:36
 * Class Name: DatabaseVersionConfig
 * Author: Levent8421
 * Description:
 * 数据库版本配置
 * 该类结构与配置文件json结构一致
 *
 * @author Levent8421
 */
@Data
public class DatabaseVersionConfig {
    /**
     * 目标数据库版本
     */
    private String targetDbVersion;
    /**
     * 数据库更新脚本位置
     */
    private String sqlScriptPath;
}
