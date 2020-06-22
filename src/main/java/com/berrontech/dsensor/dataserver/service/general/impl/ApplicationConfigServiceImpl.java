package com.berrontech.dsensor.dataserver.service.general.impl;

import com.berrontech.dsensor.dataserver.common.entity.ApplicationConfig;
import com.berrontech.dsensor.dataserver.repository.mapper.ApplicationConfigMapper;
import com.berrontech.dsensor.dataserver.service.basic.impl.AbstractServiceImpl;
import com.berrontech.dsensor.dataserver.service.general.ApplicationConfigService;
import lombok.val;
import org.springframework.stereotype.Service;

/**
 * Create By Levent8421
 * Create Time: 2020/6/22 18:37
 * Class Name: ApplicationConfigServiceImpl
 * Author: Levent8421
 * Description:
 * 系统配置相关业务组件实现
 *
 * @author Levent8421
 */
@Service
public class ApplicationConfigServiceImpl extends AbstractServiceImpl<ApplicationConfig> implements ApplicationConfigService {
    private final ApplicationConfigMapper applicationConfigMapper;

    public ApplicationConfigServiceImpl(ApplicationConfigMapper applicationConfigMapper) {
        super(applicationConfigMapper);
        this.applicationConfigMapper = applicationConfigMapper;
    }

    @Override
    public ApplicationConfig getConfig(String name) {
        val query = new ApplicationConfig();
        query.setName(name);
        return findOneByQuery(query);
    }

    @Override
    public ApplicationConfig setConfig(String name, String value) {
        ApplicationConfig config = getConfig(name);
        if (config == null) {
            config = new ApplicationConfig();
            config.setName(name);
            config.setValue(value);
            return save(config);
        } else {
            config.setValue(value);
            return updateById(config);
        }
    }
}
