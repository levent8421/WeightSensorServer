package com.berrontech.dsensor.dataserver.service.general.impl;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.repository.mapper.DeviceConnectionMapper;
import com.berrontech.dsensor.dataserver.service.basic.impl.AbstractServiceImpl;
import com.berrontech.dsensor.dataserver.service.general.DeviceConnectionService;
import com.berrontech.dsensor.dataserver.weight.serial.util.SerialDeviceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
@Slf4j
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
        replaceUsbPath(param);
        return save(param);
    }

    @Override
    public List<DeviceConnection> refreshConnectionUsbIdAndGet() {
        final List<DeviceConnection> connections = all();
        return connections.stream()
                .map(this::refreshUsbId)
                .collect(Collectors.toList());
    }

    @Override
    public DeviceConnection refreshUsbId(DeviceConnection connection) {
        if (!Objects.equals(connection.getType(), DeviceConnection.TYPE_SERIAL)) {
            return connection;
        }
        final String deviceName = connection.getTarget();
        try {
            final File usbIdFile = SerialDeviceUtils.getUsbDeviceIdPath(deviceName);
            if (usbIdFile == null) {
                log.warn("Can not find USB ID FILE for connection:[{}]", connection.getTarget());
                return connection;
            }
            connection.setTarget(usbIdFile.getAbsolutePath());
            connection.setUsbDeviceId(usbIdFile.getName());
            return updateById(connection);
        } catch (IOException e) {
            log.error("Error on refresh ttyDevice USB ID", e);
            return connection;
        }
    }

    private void replaceUsbPath(DeviceConnection connection) {
        if (!Objects.equals(connection.getType(), DeviceConnection.TYPE_SERIAL)) {
            return;
        }
        final String devicePath = connection.getTarget();
        String usbId = null;
        try {
            usbId = SerialDeviceUtils.getUsbTtyDeviceId(devicePath);
        } catch (IOException e) {
            log.debug("Error on replace device path to usbId!devicePath=" + devicePath, e);
        }
        if (usbId == null) {
            return;
        }
        connection.setUsbDeviceId(usbId);
        connection.setTarget(SerialDeviceUtils.asUsbDeviceIdTarget(usbId));
    }
}
