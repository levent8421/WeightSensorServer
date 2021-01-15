package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.common.entity.ApplicationConfig;
import com.berrontech.dsensor.dataserver.common.util.ProcessUtils;
import com.berrontech.dsensor.dataserver.conf.ApplicationConfiguration;
import com.berrontech.dsensor.dataserver.conf.SerialConfiguration;
import com.berrontech.dsensor.dataserver.service.general.ApplicationConfigService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.web.vo.DashboardData;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryTemperatureHumiditySensor;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Create By Levent8421
 * Create Time: 2020/6/24 15:56
 * Class Name: DashboardController
 * Author: Levent8421
 * Description:
 * 数据看板相关数据访问控制器
 *
 * @author Levent8421
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController extends AbstractController {
    private final WeightDataHolder weightDataHolder;
    private final ApplicationConfigService applicationConfigService;
    private final SerialConfiguration serialConfiguration;
    private final ApplicationConfiguration applicationConfiguration;

    public DashboardController(WeightDataHolder weightDataHolder,
                               ApplicationConfigService applicationConfigService,
                               SerialConfiguration serialConfiguration,
                               ApplicationConfiguration applicationConfiguration) {
        this.weightDataHolder = weightDataHolder;
        this.applicationConfigService = applicationConfigService;
        this.serialConfiguration = serialConfiguration;
        this.applicationConfiguration = applicationConfiguration;
    }

    /**
     * 获取当前实时称重数据
     *
     * @return GR with weight data
     */
    @GetMapping("/slot-data")
    public GeneralResult<Map<String, MemorySlot>> slotData() {
        return GeneralResult.ok(weightDataHolder.getSlotTable());
    }

    /**
     * 温斯底传感器数据
     *
     * @return GR
     */
    @GetMapping("/_th-data")
    public GeneralResult<Map<Integer, MemoryTemperatureHumiditySensor>> temperatureHumiditySensorData() {
        final Map<Integer, MemoryTemperatureHumiditySensor> sensorMap = weightDataHolder.getTemperatureHumiditySensorTable();
        return GeneralResult.ok(sensorMap);
    }

    /**
     * 数据看板界面需要的数据
     *
     * @return GR
     */
    @GetMapping("/_data")
    public GeneralResult<DashboardData> dashboardData() {
        final Map<String, MemorySlot> slotData = weightDataHolder.getSlotTable();
        final Map<Integer, MemoryTemperatureHumiditySensor> temperatureHumidityData = weightDataHolder.getTemperatureHumiditySensorTable();
        final DashboardData dashboardData = new DashboardData();
        dashboardData.setSlotData(slotData);
        dashboardData.setTemperatureHumidityData(temperatureHumidityData);
        return GeneralResult.ok(dashboardData);
    }

    /**
     * 系统信息
     *
     * @return GR
     */
    @GetMapping("/system-infos")
    public GeneralResult<Map<String, Object>> systemInfos() {
        final Map<String, Object> res = new HashMap<>(16);
        final String appVersion = applicationConfiguration.getAppVersion();
        res.put("appVersion", appVersion);
        res.put("appName", ApplicationConstants.Context.APP_NAME);
        res.put("pid", ProcessUtils.getProcessId());
        res.put("libPath", serialConfiguration.getLibName());

        final List<ApplicationConfig> configs = applicationConfigService.all();
        for (ApplicationConfig config : configs) {
            res.put(config.getName(), config.getValue());
        }
        return GeneralResult.ok(res);
    }

    /**
     * 系统配置
     *
     * @return GR
     */
    @GetMapping("/system-props")
    public GeneralResult<Properties> systemProperties() {
        return GeneralResult.ok(System.getProperties());
    }
}
