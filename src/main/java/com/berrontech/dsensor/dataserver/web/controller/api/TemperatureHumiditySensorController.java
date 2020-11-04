package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.TemperatureHumiditySensor;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.service.general.TemperatureHumiditySensorService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.web.vo.TemperatureCalibrationParam;
import com.berrontech.dsensor.dataserver.weight.CalibrationException;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notEmpty;
import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notNull;

/**
 * Create By Levent8421
 * Create Time: 2020/9/18 15:59
 * Class Name: TemperatureHumiditySensorController
 * Author: Levent8421
 * Description:
 * 温湿度传感器相关数据访问控制器
 *
 * @author Levent8421
 */
@RestController
@RequestMapping("/api/th-sensor")
public class TemperatureHumiditySensorController extends AbstractController {
    private final TemperatureHumiditySensorService temperatureHumiditySensorService;
    private final WeightController weightController;

    public TemperatureHumiditySensorController(TemperatureHumiditySensorService temperatureHumiditySensorService,
                                               WeightController weightController) {
        this.temperatureHumiditySensorService = temperatureHumiditySensorService;
        this.weightController = weightController;
    }

    /**
     * All Sensors
     *
     * @return GR
     */
    @GetMapping("/")
    public GeneralResult<List<TemperatureHumiditySensor>> findAll() {
        final List<TemperatureHumiditySensor> sensors = temperatureHumiditySensorService.all();
        return GeneralResult.ok(sensors);
    }

    /**
     * 设置温湿度报警范围
     *
     * @param id    id
     * @param param params
     * @return GR
     */
    @PostMapping("/{id}/_range")
    public GeneralResult<TemperatureHumiditySensor> setRange(@PathVariable("id") Integer id,
                                                             @RequestBody TemperatureHumiditySensor param) {
        final TemperatureHumiditySensor sensor = temperatureHumiditySensorService.require(id);
        checkAndCopySetRangeParam(sensor, param);
        final TemperatureHumiditySensor res = temperatureHumiditySensorService.updateById(sensor);
        return GeneralResult.ok(res);
    }

    private void checkAndCopySetRangeParam(TemperatureHumiditySensor sensor, TemperatureHumiditySensor param) {
        final Class<? extends RuntimeException> error = BadRequestException.class;
        notNull(param, error, "No Params!");
        notNull(param.getMinTemperature(), error, "Please enter min temp!");
        notNull(param.getMaxTemperature(), error, "Please enter max temp!");
        notNull(param.getMinHumidity(), error, "Please enter min humidity!");
        notNull(param.getMaxHumidity(), error, "Please enter max humidity!");

        sensor.setMinTemperature(param.getMinTemperature());
        sensor.setMaxTemperature(param.getMaxTemperature());
        sensor.setMinHumidity(param.getMinHumidity());
        sensor.setMaxHumidity(param.getMaxHumidity());
    }

    /**
     * Find sensor by id
     *
     * @param id id
     * @return GR
     */
    @GetMapping("/{id}")
    public GeneralResult<TemperatureHumiditySensor> findSensor(@PathVariable("id") Integer id) {
        final TemperatureHumiditySensor sensor = temperatureHumiditySensorService.require(id);
        return GeneralResult.ok(sensor);
    }

    /**
     * 标定温度
     *
     * @param id    传感器ID
     * @param param 参数
     * @return GR
     */
    @PostMapping("/{id}/_calibrate-temperature")
    public GeneralResult<Void> calibrateTemperature(@PathVariable("id") Integer id,
                                                    @RequestBody TemperatureCalibrationParam param) {
        notNull(param, BadRequestException.class, "Null params!");
        notEmpty(param.getCurrentTemperature(), BadRequestException.class, "No temp!");
        final TemperatureHumiditySensor sensor = temperatureHumiditySensorService.require(id);
        final BigDecimal temp;
        try {
            temp = new BigDecimal(param.getCurrentTemperature());
        } catch (Exception e) {
            throw new BadRequestException("Invalidate temp! " + param.getCurrentTemperature());
        }

        try {
            weightController.calibrateTemperatureSensor(sensor.getConnectionId(), sensor.getAddress(), temp);
        } catch (CalibrationException e) {
            final String err = String.format("[%s:%s]", e.getClass().getSimpleName(), e.getMessage());
            throw new InternalServerErrorException(err, e);
        }
        return GeneralResult.ok();
    }

}
