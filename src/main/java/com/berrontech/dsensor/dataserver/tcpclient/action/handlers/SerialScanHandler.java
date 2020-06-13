package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.weight.serial.util.SerialUtils;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import lombok.val;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 18:17
 * Class Name: SerialScanHandler
 * Author: Levent8421
 * Description:
 *
 * @author Levent8421
 */
@ActionHandlerMapping("serial.scan")
public class SerialScanHandler implements ActionHandler {
    @Override
    public Message onMessage(Message message) {
        val ports = SerialUtils.scan();
        val payload = Payload.ok(ports);
        return MessageUtils.replyMessage(message, payload);
    }
}
