package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.web.vo.FirmwareUpgradeProgress;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.firmware.FirmwareLoader;
import com.berrontech.dsensor.dataserver.weight.firmware.FirmwareResource;
import com.berrontech.dsensor.dataserver.weight.firmware.UpgradeFirmwareListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final FirmwareUpgradeProgress upgradeProgress = new FirmwareUpgradeProgress();
    private Integer upgradeConnectionId = null;
    private Integer upgradeAddress = null;

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
        tryAbortFirmwareUpgrade();
        upgradeProgress.setState(FirmwareUpgradeProgress.STATE_INIT);
        upgradeProgress.setCurrent(0);
        upgradeProgress.setTotal(0);
        upgradeProgress.setAddress(sensor.getAddress());
        weightController.upgradeFirmware(sensor.getConnectionId(), sensor.getAddress(), resource, this);
        return GeneralResult.ok();
    }

    private void tryAbortFirmwareUpgrade() {
        if (upgradeConnectionId != null && upgradeAddress != null) {
            weightController.cancelUpgrade(upgradeConnectionId, upgradeAddress);
        }
        upgradeConnectionId = null;
        upgradeAddress = null;
    }

    /**
     * 固件升级进程
     *
     * @return GR
     */
    @GetMapping("/_upgrade-progress")
    public GeneralResult<FirmwareUpgradeProgress> upgradeProgress() {
        return GeneralResult.ok(upgradeProgress);
    }

    /**
     * 取消升级
     *
     * @return GR
     */
    @PostMapping("/_abort-upgrade")
    public GeneralResult<Void> abortUpgrade() {
        tryAbortFirmwareUpgrade();
        return GeneralResult.ok();
    }

    @Override
    public void onUpdate(int totalLen, int currentPos) {
        log.debug("Update: [{}/{}]", currentPos, totalLen);
        upgradeProgress.setState(FirmwareUpgradeProgress.STATE_PROGRESS);
        upgradeProgress.setTotal(totalLen);
        upgradeProgress.setCurrent(currentPos);
    }

    @Override
    public void onSuccess(Integer connectionId, Integer address) {
        log.debug("Success: [{}/{}]", connectionId, address);
        upgradeProgress.setState(FirmwareUpgradeProgress.STATE_SUCCESS);
    }

    @Override
    public void onError(Integer connectionId, Integer address, Throwable error) {
        log.debug("Error: [{}/{}]", connectionId, address, error);
        upgradeProgress.setState(FirmwareUpgradeProgress.STATE_FAIL);
    }
}
