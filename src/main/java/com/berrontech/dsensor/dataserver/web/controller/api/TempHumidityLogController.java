package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.TempHumidityLog;
import com.berrontech.dsensor.dataserver.service.general.TempHumidityLogService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/9/24 14:30
 * Class Name: TempHumidityLogController
 * Author: Levent8421
 * Description:
 * 温湿度日志相关数据访问控制器
 *
 * @author Levent8421
 */
@RestController
@RequestMapping("/api/temp-humidity-log")
public class TempHumidityLogController extends AbstractController {
    private final TempHumidityLogService tempHumidityLogService;

    public TempHumidityLogController(TempHumidityLogService tempHumidityLogService) {
        this.tempHumidityLogService = tempHumidityLogService;
    }

    /**
     * 传感器的温湿度日志
     *
     * @param sensorId 传感器ID
     * @return GR
     */
    @GetMapping("/_sensor-logs")
    public GeneralResult<List<TempHumidityLog>> findSensorLogs(@RequestParam("sensorId") Integer sensorId) {
        final List<TempHumidityLog> logs = tempHumidityLogService.findBySensor(sensorId);
        return GeneralResult.ok(logs);
    }
}
