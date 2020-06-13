package com.berrontech.dsensor.dataserver.tcpclient.util;

import com.alibaba.fastjson.JSONObject;
import com.berrontech.dsensor.dataserver.common.util.DateTimeUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 11:53
 * Class Name: MessageUtils
 * Author: Levent8421
 * Description:
 * Message Utils
 *
 * @author Levent8421
 */
@Slf4j
public class MessageUtils {
    private static final String SEQ_NO_FORMAT = "yyyyMMddHHmmssSSS";

    public static Message requestMessage(String seqNo, String action, Object payload) {
        return asMessage(Message.TYPE_REQUEST, seqNo, action, payload);
    }

    public static Message responseMessage(String seqNo, String action, Payload<?> payload) {
        return asMessage(Message.TYPE_RESPONSE, seqNo, action, payload);
    }

    public static Message asMessage(String type, String seqNo, String action, Object payload) {
        val message = new Message();
        message.setAction(action);
        message.setPayload(payload);
        message.setSeqNo(seqNo);
        message.setType(type);
        return message;
    }

    public static String nextSeqNo() {
        return DateTimeUtils.format(DateTimeUtils.now(), SEQ_NO_FORMAT);
    }

    public static Message replyMessage(Message message, Payload<?> payload) {
        return responseMessage(message.getSeqNo(), message.getAction(), payload);
    }

    @SuppressWarnings("unchecked")
    public static <T> Payload<T> asResponsePayload(Object payload, Class<T> dataType) {
        if (payload == null) {
            return null;
        }
        if (payload instanceof Payload) {
            return (Payload<T>) payload;
        }
        if (payload instanceof JSONObject) {
            final JSONObject json = (JSONObject) payload;
            final Payload<T> target = new Payload<>();
            target.setCode(json.getInteger("code"));
            target.setMsg(json.getString("msg"));
            if (dataType != null) {
                target.setData(json.getObject("data", dataType));
            }
            return target;
        }
        log.warn("Could Not Convert a [{}] to [{}]", payload.getClass().getName(), Payload.class.getName());
        return null;
    }
}
