package com.berrontech.dsensor.dataserver.web.vo;

import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/10/26 19:24
 * Class Name: ConfigSettingParam
 * Author: Levent8421
 * Description:
 * 系统配置参数
 *
 * @author Levent8421
 */
@Data
public class ConfigSettingParam {
    /**
     * 配置名称
     */
    private String name;
    /**
     * 配置值
     */
    private String value;
    /**
     * 不存在时是否创建
     */
    private Boolean createIfNotExists;
}
