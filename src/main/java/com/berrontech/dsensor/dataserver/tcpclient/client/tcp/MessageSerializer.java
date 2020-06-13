package com.berrontech.dsensor.dataserver.tcpclient.client.tcp;

import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;

/**
 * Create By Levent8421
 * Create Time: 2020/6/10 17:15
 * Class Name: MessageSerialize
 * Author: Levent8421
 * Description:
 * Message Serializer
 *
 * @author Levent8421
 */
public interface MessageSerializer {
    /**
     * Serialize Message
     *
     * @param message message object
     * @return bytes
     */
    byte[] serialize(Message message);

    /**
     * Deserialize Message
     *
     * @param data data bytes
     * @return Message object
     * @throws MessageException throw on there is an error
     */
    Message deserialization(byte[] data) throws MessageException;
}
