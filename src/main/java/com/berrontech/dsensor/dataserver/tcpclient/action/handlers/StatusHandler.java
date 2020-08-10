package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.common.entity.ApplicationConfig;
import com.berrontech.dsensor.dataserver.common.util.ProcessUtils;
import com.berrontech.dsensor.dataserver.conf.ApplicationConfiguration;
import com.berrontech.dsensor.dataserver.service.general.ApplicationConfigService;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.RuokVo;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 15:36
 * Class Name: StatusHandler
 * Author: Levent8421
 * Description:
 * Service Status Action Handler
 *
 * @author Levent8421
 */
@ActionHandlerMapping("ruok.get")
@Slf4j
public class StatusHandler implements ActionHandler {
    private final ApiClient apiClient;
    private final ApplicationConfigService applicationConfigService;
    private final ApplicationConfiguration applicationConfiguration;

    public StatusHandler(ApiClient apiClient,
                         ApplicationConfigService applicationConfigService,
                         ApplicationConfiguration applicationConfiguration) {
        this.apiClient = apiClient;
        this.applicationConfigService = applicationConfigService;
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    public Message onMessage(Message message) {
        val data = new RuokVo();
        data.setAppVersion(applicationConfiguration.getAppVersion());
        val dbVersion = applicationConfigService.getConfig(ApplicationConfig.DB_VERSION);
        data.setDbVersion(dbVersion == null ? "NotSet" : dbVersion.getValue());
        data.setConnectionStatus(apiClient.isConnected() ? "Connected" : "disconnected");
        data.setPid(ProcessUtils.getProcessId());
        val payload = Payload.ok(data);
        return MessageUtils.replyMessage(message, payload);
    }
}
