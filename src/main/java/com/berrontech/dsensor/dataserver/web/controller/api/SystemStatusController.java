package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.ApplicationConfig;
import com.berrontech.dsensor.dataserver.conf.ApplicationConfiguration;
import com.berrontech.dsensor.dataserver.repository.mapper.DatabaseMetaDataMapper;
import com.berrontech.dsensor.dataserver.service.general.ApplicationConfigService;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageLogger;
import com.berrontech.dsensor.dataserver.tcpclient.client.tcp.ConnectionConfiguration;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public SystemStatusController(ApiClient apiClient,
                                  ConnectionConfiguration connectionConfiguration,
                                  DatabaseMetaDataMapper databaseMetaDataMapper,
                                  MessageLogger messageLogger,
                                  ApplicationConfiguration applicationConfiguration,
                                  ApplicationConfigService applicationConfigService) {
        this.apiClient = apiClient;
        this.connectionConfiguration = connectionConfiguration;
        this.databaseMetaDataMapper = databaseMetaDataMapper;
        this.messageLogger = messageLogger;
        this.applicationConfiguration = applicationConfiguration;
        this.applicationConfigService = applicationConfigService;
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
}
