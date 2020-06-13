package com.berrontech.dsensor.dataserver.tcpclient.vo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/6/10 16:38
 * Class Name: Message
 * Author: Levent8421
 * Description:
 * Api Data Message
 *
 * @author Levent8421
 */
@Data
public class Message {
    public static final String TYPE_REQUEST = "request";
    public static final String TYPE_RESPONSE = "response";
    /**
     * Message Type
     */
    private String type;
    /**
     * Action
     */
    private String action;
    /**
     * Message ID
     */
    private String seqNo;
    /**
     * Payload
     */
    private Object payload;

    public String asJsonString() {
        return JSON.toJSONString(this);
    }

    public byte[] asJsonBytes() {
        return JSON.toJSONBytes(this);
    }
}
