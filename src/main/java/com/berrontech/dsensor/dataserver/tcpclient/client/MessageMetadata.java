package com.berrontech.dsensor.dataserver.tcpclient.client;

import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 16:07
 * Class Name: MessageMetadata
 * Author: Levent8421
 * Description:
 * 消息元数据
 *
 * @author Levent8421
 */
public class MessageMetadata {
    /**
     * 协议开头
     */
    public static final byte[] PROTOCOL_START_BYTES = {0x02};
    /**
     * 协议结尾
     */
    public static final byte[] PROTOCOL_END_BYTES = {0x03};
    /**
     * 数据包最大长度
     */
    public static final int MESSAGE_FRAME_MAS_LENGTH = 1024 * 512;
    /**
     * Message charset
     */
    public static final Charset MESSAGE_CHARSET = CharsetUtil.UTF_8;
    /**
     * 重试次数
     */
    public static final int MAX_RETRY = 3;
}
