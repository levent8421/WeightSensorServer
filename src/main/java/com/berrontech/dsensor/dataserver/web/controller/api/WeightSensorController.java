package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractEntityController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.task.SensorMetaDataService;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notNull;

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
    private final SensorMetaDataService sensorMetaDataService;
    private final WeightController weightController;

    protected WeightSensorController(WeightSensorService weightSensorService,
                                     SensorMetaDataService sensorMetaDataService,
                                     WeightController weightController) {
        super(weightSensorService);
        this.weightSensorService = weightSensorService;
        this.sensorMetaDataService = sensorMetaDataService;
        this.weightController = weightController;
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
     * 查询所有传感器 同时抓取slot信息
     *
     * @return GR
     */
    @GetMapping("/_with-slot")
    public GeneralResult<List<WeightSensor>> fetchAllWithSlot() {
        final List<WeightSensor> res = weightSensorService.listWithSlot();
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

    /**
     * 刷新内存传感器结构
     *
     * @return GR
     */
    @PostMapping("/reload")
    public GeneralResult<Void> reloadSensorMetaData() {
        sensorMetaDataService.refreshSlotTable();
        return GeneralResult.ok();
    }

    /**
     * 设置Elabel状态
     *
     * @param id    id
     * @param param params
     * @return GR
     */
    @PostMapping("/{id}/haselabel")
    public GeneralResult<Void> setElabelState(@PathVariable("id") Integer id,
                                              @RequestBody WeightSensor param) {
        notNull(param, BadRequestException.class, "No Params");
        notNull(param.getHasElabel(), BadRequestException.class, "No HasELabel Set!");
        weightSensorService.updateElableState(id, param.getHasElabel());
        return GeneralResult.ok();
    }
}
