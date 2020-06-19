package com.berrontech.dsensor.dataserver.tcpclient.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.berrontech.dsensor.dataserver.common.util.DateTimeUtils;
import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
        message.setData(payload);
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

    /**
     * As Object
     *
     * @param o    object
     * @param type target object
     * @param <T>  target type
     * @return obj
     * @throws MessageException error
     */
    @SuppressWarnings("unchecked")
    public static <T> T asObject(Object o, Class<T> type) throws MessageException {
        if (o == null) {
            return null;
        }
        if (type.isInstance(o)) {
            return (T) o;
        }
        if (o instanceof String) {
            return JSON.parseObject((String) o, type);
        }
        if (o instanceof JSONObject) {
            return ((JSONObject) o).toJavaObject(type);
        }
        throw new MessageException("Unable Convert " + o.getClass() + " to " + type);
    }

    /**
     * 转换数据对象为List
     *
     * @param o    o
     * @param type target type
     * @param <T>  类型
     * @return List
     * @throws MessageException Any Error
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> asList(Object o, Class<T> type) throws MessageException {
        if (o == null) {
            return null;
        }
        if (o instanceof JSONArray) {
            final JSONArray array = (JSONArray) o;
            val res = new ArrayList<T>();
            for (int i = 0; i < array.size(); i++) {
                res.add(array.getObject(i, type));
            }
            return res;
        }
        if (o instanceof List) {
            return (List<T>) o;
        }
        if (o instanceof Collection) {
            final Collection<T> collection = (Collection<T>) o;
            return new ArrayList<>(collection);
        }
        if (o.getClass().isArray()) {
            T[] arr = (T[]) o;
            return new ArrayList<>(Arrays.asList(arr));
        }
        throw new MessageException("Unable to convert [" + o.getClass() + "] to [List<" + type + "]");
    }
}
