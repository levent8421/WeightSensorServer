package com.berrontech.dsensor.dataserver.tcpclient.notify.impl;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.common.entity.ApplicationConfig;
import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.service.general.ApplicationConfigService;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
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
import com.berrontech.dsensor.dataserver.weight.task.SensorMetaDataService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
public class SimpleWeightNotifier implements WeightNotifier, MessageListener, ApplicationContextAware {
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
    private String dbVersion;
    /**
     * API客户端引用
     */
    private final ApiClient apiClient;
    /**
     * 记录当前数据通知失败次数
     * 当失败次数超过最大次数时认为当前连接已经失效，并主动断开TCP连接，等待系统重新连接
     */
    private int dataSendFailureTimes = 0;
    private final WeightSensorService weightSensorService;
    private final SlotService slotService;
    private final ApplicationConfigService applicationConfigService;
    private SensorMetaDataService sensorMetaDataService;
    private ApplicationContext applicationContext;

    public SimpleWeightNotifier(ApiClient apiClient,
                                WeightSensorService weightSensorService,
                                SlotService slotService,
                                ApplicationConfigService applicationConfigService) {
        this.apiClient = apiClient;
        this.weightSensorService = weightSensorService;
        this.slotService = slotService;
        this.applicationConfigService = applicationConfigService;
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
            replyPayload = MessageUtils.asObject(reply.getData(), Payload.class);
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

    /**
     * 统一处理消息发送失败事件， 累计失败次数达到指定值时，认为当前连接失效，将断开TCP连接， 等待系统再次发起连接
     *
     * @param messageInfo 发送失败的消息
     */
    private void onMessageSendFailure(MessageInfo messageInfo) {
        this.dataSendFailureTimes++;
        if (dataSendFailureTimes >= ApplicationConstants.Message.MAXIMUM_CONSECUTIVE_SEND_FAILURES) {
            apiClient.disconnect();
            log.error("Consecutive notify send errors[{}], last Error Message={}",
                    dataSendFailureTimes, asMessageString(messageInfo));
            reset();
        }
    }

    /**
     * 当发生以下任意一种情况时将当前失败次数的计数器重置为0，重新开始失败计数
     * 1. 收到任意一条消息的回应
     * 2. 失败次数达到指定值，并将TCP连接断开后，应该重新开始计数
     */
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

    /**
     * 从数据库中查询出当前数据库的版本
     *
     * @return 版本
     */
    private String getDbVersion() {
        if (this.dbVersion == null) {
            val config = applicationConfigService.getConfig(ApplicationConfig.DB_VERSION);
            if (config == null) {
                throw new InternalServerErrorException("No DbVersion Set!");
            }
            this.dbVersion = config.getValue();
        }
        return this.dbVersion;
    }

    @Override
    public void heartbeat() {
        val heartbeat = new Heartbeat();
        heartbeat.setAlive(true);
        heartbeat.setAppName(ApplicationConstants.Context.APP_NAME);
        heartbeat.setAppVersion(ApplicationConstants.Context.APP_VERSION);
        heartbeat.setDbVersion(getDbVersion());
        val seqNo = MessageUtils.nextSeqNo();
        val message = MessageUtils.requestMessage(seqNo, ApplicationConstants.Actions.HEARTBEAT, heartbeat);
        sendMessage(message);
    }

    @Override
    public void countChange(Collection<MemorySlot> slots) {
        val dataList = memoryObject2SlotVo(slots);
        val message = MessageUtils.asMessage(Message.TYPE_REQUEST, nextSeqNo(), ACTION_COUNT_CHANGED, dataList);
        sendMessage(message);
    }

    @Override
    public void deviceStateChanged(Collection<MemorySlot> slots, int state) {
        log.info("Slot State Changed: state=[{}]", state);
        val dataList = memoryObject2SlotVo(slots);
        val message = MessageUtils.asMessage(Message.TYPE_REQUEST, nextSeqNo(), ACTION_STATE_CHANGED, dataList);
        for (MemorySlot slot : slots) {
            slotService.updateState(slot.getId(), state);
        }
        sendMessage(message);
    }

    @Override
    public void notifySlotList(Collection<Slot> slots) {
        val resList = entity2SlotVo(slots);
        val message = MessageUtils.asMessage(Message.TYPE_REQUEST, nextSeqNo(), ACTION_BALANCE_LIST, resList);
        sendMessage(message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyScanDone(Collection<MemoryWeightSensor> sensors) {
        final List<Slot> slots = createOrUpdateSlot(sensors);
        notifySlotList(slots);
        obtainSensorMetaDataService().refreshSlotTable();
    }

    @Override
    public void sensorStateChanged(Collection<MemoryWeightSensor> sensors) {
        obtainSensorMetaDataService().updateSensorStateInSlotTable(sensors);
        final Map<Integer, SlotVo> slotMap = new HashMap<>(16);
        for (MemoryWeightSensor sensor : sensors) {
            weightSensorService.updateState(sensor.getId(), sensor.getState());
            if (!slotMap.containsKey(sensor.getId())) {
                val slot = slotService.get(sensor.getSlotId());
                val ms = MemorySlot.of(slot);
                ms.setState(sensor.getState());
                val vo = SlotVo.of(ms);
                slotMap.put(vo.getId(), vo);
                log.debug("Update slot[{},{}], sensor[{{},{}}] state to [{}]",
                        slot.getId(), slot.getSlotNo(), slot.getId(), slot.getAddress(), sensor.getState());
            }
        }
        val slots = new ArrayList<>(slotMap.values());
        val message = MessageUtils.asMessage(Message.TYPE_REQUEST, nextSeqNo(), ACTION_STATE_CHANGED, slots);
        sendMessage(message);
    }

    private List<Slot> createOrUpdateSlot(Collection<MemoryWeightSensor> sensors) {
        final List<WeightSensor> weightSensors = weightSensorService.createOrUpdateSensor(sensors);
        return slotService.createOrUpdateSlotsBySensor(weightSensors, weightSensorService);
    }

    private String nextSeqNo() {
        return MessageUtils.nextSeqNo();
    }

    private List<SlotVo> entity2SlotVo(Collection<Slot> slots) {
        return slots.stream()
                .map(MemorySlot::of)
                .map(SlotVo::of)
                .collect(Collectors.toList());
    }

    private List<SlotVo> memoryObject2SlotVo(Collection<MemorySlot> slots) {
        return slots.stream().map(this::memoryObject2SlotVo).collect(Collectors.toList());
    }

    private SlotVo memoryObject2SlotVo(MemorySlot slot) {
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 懒加载SensorMetaDataService Component 避免Spring循环依赖出现
     *
     * @return SensorMateDataService
     */
    private SensorMetaDataService obtainSensorMetaDataService() {
        if (this.sensorMetaDataService == null) {
            this.sensorMetaDataService = this.applicationContext.getBean(SensorMetaDataService.class);
        }
        return this.sensorMetaDataService;
    }
}
