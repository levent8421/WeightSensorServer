package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.service.general.DeviceConnectionService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractEntityController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/6/16 18:35
 * Class Name: DeviceConnectionController
 * Author: Levent8421
 * Description:
 * 设备连接相关数据访问控制器
 *
 * @author Levent8421
 */
@RestController
@RequestMapping("/api/device-connection")
public class DeviceConnectionController extends AbstractEntityController<DeviceConnection> {
    private final DeviceConnectionService deviceConnectionService;

    protected DeviceConnectionController(DeviceConnectionService deviceConnectionService) {
        super(deviceConnectionService);
        this.deviceConnectionService = deviceConnectionService;
    }

    /**
     * Find all connection from database
     *
     * @return GR with device connection list
     */
    @GetMapping("/")
    public GeneralResult<List<DeviceConnection>> listAll() {
        val connections = deviceConnectionService.all();
        return GeneralResult.ok(connections);
    }
}
