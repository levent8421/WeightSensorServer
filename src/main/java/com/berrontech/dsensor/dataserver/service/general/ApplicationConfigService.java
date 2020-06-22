package com.berrontech.dsensor.dataserver.service.general;

import com.berrontech.dsensor.dataserver.common.entity.ApplicationConfig;
import com.berrontech.dsensor.dataserver.service.basic.AbstractService;

/**
 * Create By Levent8421
 * Create Time: 2020/6/22 18:37
 * Class Name: ApplicationConfigService
 * Author: Levent8421
 * Description:
 * 系统配置相关业务组件
 *
 * @author Levent8421
 */
public interface ApplicationConfigService extends AbstractService<ApplicationConfig> {
    /**
     * 获取配置
     *
     * @param name 名称
     * @return 配置
     */
    ApplicationConfig getConfig(String name);

    /**
     * 设置配置
     *
     * @param name  名称
     * @param value 配置值
     * @return config
     */
    ApplicationConfig setConfig(String name, String value);
}
