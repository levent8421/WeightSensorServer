package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.service.general.DeviceConnectionService;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractEntityController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notEmpty;
import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notNull;

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
@RequestMapping("/api/connection")
public class DeviceConnectionController extends AbstractEntityController<DeviceConnection> {
    private final DeviceConnectionService deviceConnectionService;
    private final WeightSensorService weightSensorService;

    protected DeviceConnectionController(DeviceConnectionService deviceConnectionService, WeightSensorService weightSensorService) {
        super(deviceConnectionService);
        this.deviceConnectionService = deviceConnectionService;
        this.weightSensorService = weightSensorService;
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

    /**
     * 新增连接
     *
     * @param param 参数
     * @return GR
     */
    @PutMapping("/")
    public GeneralResult<DeviceConnection> createNew(@RequestBody DeviceConnection param) {
        checkCreateParam(param);
        val res = deviceConnectionService.createConnection(param);
        return GeneralResult.ok(res);
    }

    private void checkCreateParam(DeviceConnection param) {
        val ex = BadRequestException.class;
        notNull(param, ex, "No Param!");
        notNull(param.getType(), ex, "No Type!");
        notEmpty(param.getTarget(), ex, "No Target!");
    }

    /**
     * 删除连接
     *
     * @param id id
     * @return GR
     */
    @DeleteMapping("/{id}")
    public GeneralResult<Void> delete(@PathVariable("id") Integer id) {
        deviceConnectionService.deleteById(id);
        weightSensorService.deleteByConnection(id);
        return GeneralResult.ok();
    }
}
