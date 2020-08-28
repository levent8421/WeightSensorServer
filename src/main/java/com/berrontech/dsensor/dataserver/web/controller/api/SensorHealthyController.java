package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.web.vo.WeightSensorHealthyVo;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.dto.SensorPackageCounter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Create By Levent8421
 * Create Time: 2020/8/28 11:28
 * Class Name: SensorHealthyController
 * Author: Levent8421
 * Description:
 * 传感器健康状况相关数据访问控制器
 *
 * @author Levent8421
 */
@RestController
@RequestMapping("/api/sensor-healthy")
public class SensorHealthyController extends AbstractController {
    private final WeightSensorService weightSensorService;
    private final WeightController weightController;

    public SensorHealthyController(WeightSensorService weightSensorService,
                                   WeightController weightController) {
        this.weightSensorService = weightSensorService;
        this.weightController = weightController;
    }

    /**
     * 传感器健康状况
     *
     * @return GR
     */
    @GetMapping("/_healthy")
    public GeneralResult<List<WeightSensorHealthyVo>> sensorHealthy() {
        final List<WeightSensor> sensors = weightSensorService.all();
        final List<WeightSensorHealthyVo> healthyInfo = sensors.stream().map(sensor -> {
            final WeightSensorHealthyVo healthyVo = new WeightSensorHealthyVo();
            healthyVo.setSensor(sensor);
            return healthyVo;
        }).peek(healthy -> {
            final WeightSensor sensor = healthy.getSensor();
            final SensorPackageCounter packageCounter = weightController.getPackageCounter(sensor.getConnectionId(), sensor.getAddress());
            healthy.setPackageCounter(packageCounter);
        }).collect(Collectors.toList());
        return GeneralResult.ok(healthyInfo);
    }

    /**
     * Clean Package Counter
     *
     * @return GR
     */
    @PostMapping("/_clean-counter")
    public GeneralResult<Void> cleanCounter() {
        weightController.cleanPackageCounter();
        return GeneralResult.ok();
    }
}
