package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.firmware.FirmwareLoader;
import com.berrontech.dsensor.dataserver.weight.firmware.FirmwareResource;
import com.berrontech.dsensor.dataserver.weight.firmware.UpgradeFirmwareListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create By Levent8421
 * Create Time: 2020/9/11 15:27
 * Class Name: FirmwareController
 * Author: Levent8421
 * Description:
 * 传感器固件相关数据访问控制器
 *
 * @author Levent8421
 */
@RestController
@RequestMapping("/api/firmware")
@Slf4j
public class FirmwareController extends AbstractController implements UpgradeFirmwareListener {
    private final FirmwareLoader firmwareLoader;
    private final WeightSensorService weightSensorService;
    private final WeightController weightController;

    public FirmwareController(FirmwareLoader firmwareLoader,
                              WeightSensorService weightSensorService,
                              WeightController weightController) {
        this.firmwareLoader = firmwareLoader;
        this.weightSensorService = weightSensorService;
        this.weightController = weightController;
    }

    /**
     * 加载固件文件到内存
     *
     * @return GR
     */
    @PostMapping("/_load")
    public ResponseEntity<byte[]> loadFirmware() {
        final FirmwareResource resource = firmwareLoader.loadResource().getFirmwareResource();
        log.debug("Firmware name [{}]", resource.getFileName());
        return ResponseEntity.ok(resource.getContent());
    }

    /**
     * 升级固件
     *
     * @param sensorId sensor ID
     * @return GR
     */
    @PostMapping("/{id}/_upgrade")
    public GeneralResult<Void> upgrade(@PathVariable("id") Integer sensorId) {
        final WeightSensor sensor = weightSensorService.require(sensorId);
        final FirmwareResource resource = firmwareLoader.loadResource().getFirmwareResource();
        weightController.upgradeFirmware(sensor.getConnectionId(), sensor.getAddress(), resource, this);
        return GeneralResult.ok();
    }

    @Override
    public void onUpdate(int totalLen, int currentPos) {

    }

    @Override
    public void onSuccess(Integer connectionId, Integer address) {

    }

    @Override
    public void onError(Integer connectionId, Integer address, Throwable error) {

    }
}
