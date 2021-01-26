package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.AbstractEntity;
import com.berrontech.dsensor.dataserver.common.entity.ApplicationConfig;
import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.conf.ApplicationConfiguration;
import com.berrontech.dsensor.dataserver.repository.mapper.DatabaseMetaDataMapper;
import com.berrontech.dsensor.dataserver.service.general.ApplicationConfigService;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageLogger;
import com.berrontech.dsensor.dataserver.tcpclient.client.ConnectionConfiguration;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.upgrade.DatabaseUpgrader;
import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.dto.DeviceDetails;
import com.berrontech.dsensor.dataserver.weight.dto.SystemError;
import com.berrontech.dsensor.dataserver.weight.task.SensorMetaDataService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create By Levent8421
 * Create Time: 2020/7/27 13:38
 * Class Name: SystemStatusController
 * Author: Levent8421
 * Description:
 * 系统状态相关数据访问控制器
 *
 * @author Levent8421
 */
@RestController
@RequestMapping("/api/status")
public class SystemStatusController extends AbstractController {
    private final ApiClient apiClient;
    private final ConnectionConfiguration connectionConfiguration;
    private final DatabaseMetaDataMapper databaseMetaDataMapper;
    private final MessageLogger messageLogger;
    private final ApplicationConfiguration applicationConfiguration;
    private final ApplicationConfigService applicationConfigService;
    private final DatabaseUpgrader databaseUpgrader;
    private final SensorMetaDataService sensorMetaDataService;
    private final WeightSensorService weightSensorService;
    private final SlotService slotService;
    private final WeightController weightController;

    public SystemStatusController(ApiClient apiClient,
                                  ConnectionConfiguration connectionConfiguration,
                                  DatabaseMetaDataMapper databaseMetaDataMapper,
                                  MessageLogger messageLogger,
                                  ApplicationConfiguration applicationConfiguration,
                                  ApplicationConfigService applicationConfigService,
                                  DatabaseUpgrader databaseUpgrader,
                                  SensorMetaDataService sensorMetaDataService,
                                  WeightSensorService weightSensorService,
                                  SlotService slotService,
                                  WeightController weightController) {
        this.apiClient = apiClient;
        this.connectionConfiguration = connectionConfiguration;
        this.databaseMetaDataMapper = databaseMetaDataMapper;
        this.messageLogger = messageLogger;
        this.applicationConfiguration = applicationConfiguration;
        this.applicationConfigService = applicationConfigService;
        this.databaseUpgrader = databaseUpgrader;
        this.sensorMetaDataService = sensorMetaDataService;
        this.weightSensorService = weightSensorService;
        this.slotService = slotService;
        this.weightController = weightController;
    }

    /**
     * 系统状态
     *
     * @return GR
     */
    @GetMapping("/")
    public GeneralResult<Map<String, Object>> statusTable() {
        final Map<String, Object> status = new HashMap<>(16);
        status.put("tcpApi", getTcpApiStatus());
        return GeneralResult.ok(status);
    }

    /**
     * TCP 连接状态
     *
     * @return map
     */
    private Map<String, Object> getTcpApiStatus() {
        final Map<String, Object> status = new HashMap<>(16);
        status.put("connection", apiClient.isConnected());
        status.put("ip", connectionConfiguration.getIp());
        status.put("port", connectionConfiguration.getPort());
        return status;
    }

    /**
     * Reconnect
     *
     * @return GR
     */
    @PostMapping("/tcp-disconnect")
    private GeneralResult<Void> reconnectTcpApi() {
        apiClient.disconnect();
        return GeneralResult.ok();
    }

    /**
     * 测试连接数据库，并查询系统所有表名
     *
     * @return GR
     */
    @GetMapping("/tables")
    private GeneralResult<List<String>> tables() {
        final List<String> tables = databaseMetaDataMapper.showTables();
        return GeneralResult.ok(tables);
    }

    /**
     * Fetch All Message Log
     *
     * @return GR
     */
    @GetMapping("/message-log")
    public GeneralResult<List<Message>> messageLog() {
        final List<Message> messages = messageLogger.messageList();
        return GeneralResult.ok(messages);
    }

    /**
     * Get App Version
     *
     * @return version string
     */
    @GetMapping("/app-version")
    public String getAppVersion() {
        return applicationConfiguration.getAppVersion();
    }

    /**
     * Get Db Version
     *
     * @return version string
     */
    @GetMapping("/db-version")
    public String getDbVersion() {
        final ApplicationConfig config = applicationConfigService.getConfig(ApplicationConfig.DB_VERSION_NAME);
        if (config == null) {
            return "Null";
        }
        return config.getValue();
    }

    /**
     * 重置数据库
     *
     * @return GR
     */
    @PostMapping("/_db-reset")
    public GeneralResult<Void> resetDatabase() {
        databaseUpgrader.resetDatabase();
        sensorMetaDataService.refreshSlotTable();
        return GeneralResult.ok();
    }

    /**
     * 获取系统错误列表
     *
     * @return GR
     */
    @GetMapping("/_errors")
    public GeneralResult<?> systemErrors() {
        final List<SystemError> errors = sensorMetaDataService.getSensorErrors();
        return GeneralResult.ok(errors);
    }

    /**
     * 获取传感器详细信息
     *
     * @param address 地址
     * @return GR
     */
    @GetMapping("/{address}/_details")
    public GeneralResult<DeviceDetails> sensorDetails(@PathVariable("address") Integer address) {
        final List<WeightSensor> sensors = weightSensorService.findByAddress(address);
        if (sensors.size() != 1) {
            throw new InternalServerErrorException("duplicate or empty sensor for address " + address);
        }
        final WeightSensor sensor = sensors.get(0);
        final Slot slot = slotService.findByAddress(address);
        final DeviceDetails deviceDetails = getDeviceDetails(sensor, slot);
        return GeneralResult.ok(deviceDetails);
    }

    private DeviceDetails getDeviceDetails(WeightSensor sensor, Slot slot) {
        final DeviceDetails deviceDetails = weightController.getSensorDetails(sensor.getConnectionId(), sensor.getAddress());
        if (deviceDetails == null) {
            throw new InternalServerErrorException("Fail to get device details!");
        }
        return deviceDetails.set(DeviceDetails.SENSOR, sensor)
                .set(DeviceDetails.SLOT, slot);
    }

    /**
     * 全部传感器的详细信息
     *
     * @return GR
     */
    @GetMapping("/_all-sensor-details")
    public GeneralResult<Map<Integer, Object>> allSensorDeviceDetails() {
        final List<WeightSensor> sensors = weightSensorService.all();
        final List<Slot> slots = slotService.all();
        final Map<Integer, Slot> slotMap = slots.stream().collect(Collectors.toMap(AbstractEntity::getId, s -> s));

        final Map<Integer, Object> res = new HashMap<>(128);
        for (WeightSensor sensor : sensors) {
            final Slot slot = slotMap.get(sensor.getSlotId());
            if (res.containsKey(sensor.getAddress())) {
                res.put(sensor.getAddress() + 1000, "duplicate address");
            }
            try {
                final DeviceDetails deviceDetails = getDeviceDetails(sensor, slot);
                res.put(sensor.getAddress(), deviceDetails);
            } catch (Exception e) {
                res.put(sensor.getAddress(), String.format("%s:%s", e.getClass().getSimpleName(), e.getMessage()));
            }
        }
        return GeneralResult.ok(res);
    }
}
