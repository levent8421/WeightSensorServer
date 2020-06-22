package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.service.general.DeviceConnectionService;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import lombok.val;

/**
 * Create By Levent8421
 * Create Time: 2020/6/22 17:38
 * Class Name: BalanceScanHandler
 * Author: Levent8421
 * Description:
 * 扫描货道消息处理器
 *
 * @author Levent8421
 */
@ActionHandlerMapping("balance.scan")
public class BalanceScanHandler implements ActionHandler {
    private final WeightController weightController;
    private final DeviceConnectionService deviceConnectionService;


    public BalanceScanHandler(WeightController weightController, DeviceConnectionService deviceConnectionService) {
        this.weightController = weightController;
        this.deviceConnectionService = deviceConnectionService;
    }

    @Override
    public Message onMessage(Message message) throws Exception {
        val connections = deviceConnectionService.all();
        weightController.startScan(connections);
        val data = Payload.ok();
        return MessageUtils.replyMessage(message, data);
    }
}
