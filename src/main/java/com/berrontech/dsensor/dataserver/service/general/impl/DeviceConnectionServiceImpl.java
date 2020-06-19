package com.berrontech.dsensor.dataserver.service.general.impl;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.repository.mapper.DeviceConnectionMapper;
import com.berrontech.dsensor.dataserver.service.basic.impl.AbstractServiceImpl;
import com.berrontech.dsensor.dataserver.service.general.DeviceConnectionService;
import org.springframework.stereotype.Service;

/**
 * Create By Levent8421
 * Create Time: 2020/6/16 18:33
 * Class Name: DeviceConnectionServiceImpl
 * Author: Levent8421
 * Description:
 * 设备连接相关业务行为实现
 *
 * @author Levent8421
 */
@Service
public class DeviceConnectionServiceImpl extends AbstractServiceImpl<DeviceConnection> implements DeviceConnectionService {
    private final DeviceConnectionMapper deviceConnectionMapper;

    public DeviceConnectionServiceImpl(DeviceConnectionMapper deviceConnectionMapper) {
        super(deviceConnectionMapper);
        this.deviceConnectionMapper = deviceConnectionMapper;
    }

    @Override
    public DeviceConnection createConnection(DeviceConnection param) {
        final int count = deviceConnectionMapper.countByTypeAndTarget(param.getType(), param.getTarget());
        if (count > 0) {
            throw new BadRequestException("Connection Are Early Exists!");
        }
        return save(param);
    }
}
