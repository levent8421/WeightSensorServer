package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.weight.serial.SerialPort;
import lombok.val;

import java.io.IOException;

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
        try {
            val ports = SerialPort.findSerialPorts();
            val payload = Payload.ok(ports);
            return MessageUtils.replyMessage(message, payload);
        } catch (IOException e) {
            throw new InternalServerErrorException("Error on scan serial port:" + e.getMessage(), e);
        }
    }
}
