package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.service.general.DeviceConnectionService;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.DeviceConnectionVo;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Create By Levent8421
 * Create Time: 2020/6/17 15:47
 * Class Name: ConnectionGetHandler
 * Author: Levent8421
 * Description:
 * 获取连接列表
 *
 * @author Levent8421
 */
@ActionHandlerMapping("connection.get")
public class ConnectionGetHandler implements ActionHandler {
    private final DeviceConnectionService deviceConnectionService;

    public ConnectionGetHandler(DeviceConnectionService deviceConnectionService) {
        this.deviceConnectionService = deviceConnectionService;
    }

    @Override
    public Message onMessage(Message message) {
        val connections = deviceConnectionService.all();
        final List<DeviceConnectionVo> res = connections == null ? null :
                connections.stream().map(DeviceConnectionVo::of).collect(Collectors.toList());
        val payload = Payload.ok(res);
        return MessageUtils.replyMessage(message, payload);
    }
}
