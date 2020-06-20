package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractEntityController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/6/20 11:48
 * Class Name: WeightSensorController
 * Author: Levent8421
 * Description:
 * 传感器相关数据访问控制器
 *
 * @author Levent8421
 */
@RestController
@RequestMapping("/api/sensor")
public class WeightSensorController extends AbstractEntityController<WeightSensor> {
    private final WeightSensorService weightSensorService;

    protected WeightSensorController(WeightSensorService weightSensorService) {
        super(weightSensorService);
        this.weightSensorService = weightSensorService;
    }

    /**
     * 获取全部传感器
     *
     * @return GR
     */
    @GetMapping("/")
    public GeneralResult<List<WeightSensor>> all() {
        val res = weightSensorService.all();
        return GeneralResult.ok(res);
    }

    /**
     * Find Sensor By Connection
     *
     * @param connectionId Connection ID
     * @return GR
     */
    @GetMapping("/_by-connection")
    public GeneralResult<List<WeightSensor>> findSensorByConnection(@RequestParam("connectionId") Integer connectionId) {
        final List<WeightSensor> weightSensors = weightSensorService.findByConnection(connectionId);
        return GeneralResult.ok(weightSensors);
    }
}
