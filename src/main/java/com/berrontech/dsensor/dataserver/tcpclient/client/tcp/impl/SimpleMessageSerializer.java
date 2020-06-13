package com.berrontech.dsensor.dataserver.tcpclient.client.tcp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.tcpclient.client.tcp.MessageSerializer;
import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/6/10 17:24
 * Class Name: SimpleMessageSerializer
 * Author: Levent8421
 * Description:
 * Simple Message Serializer
 *
 * @author Levent8421
 */
@Component
public class SimpleMessageSerializer implements MessageSerializer {
    @Override
    public byte[] serialize(Message message) {
        if (message == null) {
            throw new NullPointerException("Can not send a null message!");
        }
        // Package Struct
        // STX JSON ETX
        final byte[] jsonBytes = message.asJsonBytes();
        final byte[] packageBytes = new byte[jsonBytes.length + 2];
        packageBytes[0] = ApplicationConstants.Message.PACKAGE_START;
        packageBytes[packageBytes.length - 1] = ApplicationConstants.Message.PACKAGE_END;
        System.arraycopy(jsonBytes, 0, packageBytes, 1, jsonBytes.length);
        return packageBytes;
    }

    @Override
    public Message deserialization(byte[] data) throws MessageException {
        final byte start = data[0];
        final byte end = data[data.length - 1];
        if (start == ApplicationConstants.Message.PACKAGE_START && end == ApplicationConstants.Message.PACKAGE_END) {
            try {
                return JSON.parseObject(data, 1, data.length - 2, ApplicationConstants.Context.DEFAULT_CHARSET, Message.class);
            } catch (JSONException e) {
                throw new MessageException("Invalidate JSONMessage", e);
            }
        }
        throw new MessageException(String.format("Invalidate Package Delimiter[%d,%d]", start, end));
    }
}
