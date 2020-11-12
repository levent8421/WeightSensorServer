package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.service.general.DeviceConnectionService;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractEntityController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.scan.SensorScanListener;
import com.berrontech.dsensor.dataserver.weight.scan.SimpleSensorScanListener;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
    private final WeightController weightController;
    private final SlotService slotService;
    private SensorScanListener scanListener;

    protected DeviceConnectionController(DeviceConnectionService deviceConnectionService,
                                         WeightSensorService weightSensorService,
                                         WeightController weightController,
                                         SlotService slotService) {
        super(deviceConnectionService);
        this.deviceConnectionService = deviceConnectionService;
        this.weightSensorService = weightSensorService;
        this.weightController = weightController;
        this.slotService = slotService;
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
        cleanUpByConnection(id);
        deviceConnectionService.deleteById(id);
        return GeneralResult.ok();
    }

    /**
     * 清除某链接下的所有货道和传感器
     *
     * @param connectionId 连接ID
     */
    private void cleanUpByConnection(int connectionId) {
        final List<WeightSensor> sensors = weightSensorService.findByConnection(connectionId);
        final List<Integer> addressList = sensors.stream().map(WeightSensor::getAddress).collect(Collectors.toList());
        slotService.deleteByAddressList(addressList);
        weightSensorService.deleteByConnection(connectionId);
    }

    /**
     * 开始扫描货道
     *
     * @param id 连接ID
     * @return GR
     */
    @PostMapping("/{id}/_scan")
    public GeneralResult<Void> startScan(@PathVariable("id") Integer id) {
        final DeviceConnection connection = deviceConnectionService.require(id);
        scanListener = SimpleSensorScanListener.resetAndGet();
        try {
            weightController.startScan(connection, scanListener);
        } catch (IOException e) {
            throw new InternalServerErrorException("Error On Scan!", e);
        }
        return GeneralResult.ok();
    }

    /**
     * 获取扫描进度
     *
     * @return GR
     */
    @GetMapping("/_scan-progress")
    public GeneralResult<SensorScanListener> getScanProgress() {
        return GeneralResult.ok(scanListener);
    }

    /**
     * 是否正在扫描
     *
     * @return GR
     */
    @GetMapping("/_scanning")
    public GeneralResult<Boolean> isScanning() {
        return GeneralResult.ok(weightController.isScanning());
    }

    /**
     * 扫描温湿度传感器设备
     *
     * @param id connection id
     * @return GR
     */
    @PostMapping("/{id}/_scan-th-device")
    public GeneralResult<Void> scanTemperatureHumiditySensors(@PathVariable("id") Integer id) {
        final DeviceConnection connection = deviceConnectionService.require(id);
        try {
            weightController.startScanTemperatureHumiditySensors(connection);
        } catch (IOException e) {
            throw new InternalServerErrorException("Error on scan", e);
        }
        return GeneralResult.ok();
    }
}
