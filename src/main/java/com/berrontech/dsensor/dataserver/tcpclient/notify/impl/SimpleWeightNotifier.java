package com.berrontech.dsensor.dataserver.tcpclient.notify.impl;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListener;
import com.berrontech.dsensor.dataserver.tcpclient.client.tcp.MessageInfo;
import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.notify.WeightNotifier;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.Heartbeat;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.SkuVo;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.SlotVo;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.WeightDataVo;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Create By Levent8421
 * Create Time: 2020/6/22 11:25
 * Class Name: SimpleWeightNotifier
 * Author: Levent8421
 * Description:
 * 重力数据相关数据通知组件
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class SimpleWeightNotifier implements WeightNotifier, MessageListener {
    /**
     * Action 通知货道数据改变
     */
    private static final String ACTION_COUNT_CHANGED = "notify.balance.weight_changed";
    /**
     * Action 通知货道状态改变
     */
    private static final String ACTION_STATE_CHANGED = "notify.balance.state";
    /**
     * Action 上报货道列表
     */
    private static final String ACTION_BALANCE_LIST = "notify.balance.list";
    /**
     * API客户端引用
     */
    private final ApiClient apiClient;
    /**
     * 记录当前数据通知失败次数
     * 当失败次数超过最大次数时认为当前连接已经失效，并主动断开TCP连接，等待系统重新连接
     */
    private int dataSendFailureTimes = 0;

    public SimpleWeightNotifier(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public void beforeSend(MessageInfo messageInfo) {
        if (log.isDebugEnabled()) {
            log.debug("Before Notify {}", asMessageString(messageInfo));
        }
    }

    @Override
    public void onTimeout(MessageInfo messageInfo) {
        log.warn("Notify Timout: {}", asMessageString(messageInfo));
        onMessageSendFailure(messageInfo);
    }

    @Override
    public void onReply(MessageInfo messageInfo, Message reply) {
        final Payload replyPayload;
        try {
            replyPayload = MessageUtils.asObject(reply, Payload.class);
        } catch (MessageException e) {
            log.warn("Error On Convert Reply Payload", e);
            onMessageSendFailure(messageInfo);
            return;
        }
        if (replyPayload == null) {
            log.warn("Notify Reply Payload=null: [{}]", asMessageString(messageInfo));
            return;
        }
        if (!Objects.equals(replyPayload.getCode(), Payload.OK)) {
            log.warn("Notify Reply Code Error[{}/{}]", replyPayload.getCode(), replyPayload.getMsg());
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Notify Reply: {}, reply=[{}]", asMessageString(messageInfo), reply.getData());
        }
        reset();
    }

    @Override
    public void onError(MessageInfo messageInfo, Throwable error) {
        log.warn("Notify Error: {}", asMessageString(messageInfo), error);
        onMessageSendFailure(messageInfo);
    }

    private void onMessageSendFailure(MessageInfo messageInfo) {
        this.dataSendFailureTimes++;
        if (dataSendFailureTimes >= ApplicationConstants.Message.MAXIMUM_CONSECUTIVE_SEND_FAILURES) {
            apiClient.disconnect();
            log.error("Consecutive notify send errors[{}], last Error Message={}",
                    dataSendFailureTimes, asMessageString(messageInfo));
            reset();
        }
    }

    private void reset() {
        this.dataSendFailureTimes = 0;
    }

    /**
     * As Message Simple String
     *
     * @param messageInfo MessageInfo
     * @return message string
     */
    private String asMessageString(MessageInfo messageInfo) {
        if (messageInfo == null || messageInfo.getMessage() == null) {
            return "[Null_Message]";
        }
        val message = messageInfo.getMessage();
        return String.format("[%s:%s/%s-%d/%d]",
                message.getType(),
                message.getSeqNo(),
                message.getAction(),
                messageInfo.getRetry(),
                messageInfo.getMaxRetry());
    }

    @Override
    public void heartbeat() {
        val heartbeat = new Heartbeat();
        heartbeat.setAlive(true);
        heartbeat.setAppName(ApplicationConstants.Context.APP_NAME);
        heartbeat.setAppVersion(ApplicationConstants.Context.APP_VERSION);
        heartbeat.setDbVersion(ApplicationConstants.Context.APP_VERSION);
        val seqNo = MessageUtils.nextSeqNo();
        val message = MessageUtils.requestMessage(seqNo, ApplicationConstants.Actions.HEARTBEAT, heartbeat);
        try {
            apiClient.send(message, ApplicationConstants.Message.MESSAGE_TIMEOUT, this);
        } catch (MessageException e) {
            log.error("Error on send heart beat!", e);
        }
    }

    @Override
    public void countChange(Collection<MemorySlot> slots) {
        val dataList = asSlotVo(slots);
        val message = MessageUtils.asMessage(Message.TYPE_REQUEST, nextSeqNo(), ACTION_COUNT_CHANGED, dataList);
        sendMessage(message);
    }

    @Override
    public void deviceStateChanged(Collection<MemorySlot> slots, int state) {
        log.info("Slot State Changed: state=[{}]", state);
        val dataList = asSlotVo(slots);
        val message = MessageUtils.asMessage(Message.TYPE_REQUEST, nextSeqNo(), ACTION_STATE_CHANGED, dataList);
        sendMessage(message);
    }

    @Override
    public void notifySensorList(Collection<MemoryWeightSensor> sensors) {
        final List<Slot> slots = createOrUpdateSlot(sensors);
        // TODO 转换slot实体为SlotVo对象 并发送通知消息
    }

    private List<Slot> createOrUpdateSlot(Collection<MemoryWeightSensor> sensors) {
        // TODO 当传感器对应的货道存在时 更新货道数据 否则创建新的货道
        return null;
    }

    private String nextSeqNo() {
        return MessageUtils.nextSeqNo();
    }

    private List<SlotVo> asSlotVo(Collection<MemorySlot> slots) {
        return slots.stream().map(this::asSlotVo).collect(Collectors.toList());
    }

    private SlotVo asSlotVo(MemorySlot slot) {
        val slotVo = SlotVo.of(slot);
        val weightData = slot.getData();
        slotVo.setData(WeightDataVo.of(weightData));
        val sku = slot.getSku();
        slotVo.setSku(SkuVo.of(sku));
        return slotVo;
    }

    private void sendMessage(Message message) {
        try {
            apiClient.send(message, ApplicationConstants.Message.MESSAGE_TIMEOUT, this);
        } catch (MessageException e) {
            log.error("Error On Send Notify Message:{}", message, e);
        }
    }
}