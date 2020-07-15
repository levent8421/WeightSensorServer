package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.weight.task.SensorMetaDataService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Create By Levent8421
 * Create Time: 2020/6/19 14:40
 * Class Name: BalanceNoSetHandler
 * Author: Levent8421
 * Description:
 * 设置逻辑货道号
 *
 * @author Levent8421
 */
@Slf4j
@ActionHandlerMapping("balance.no.set")
public class BalanceNoSetHandler implements ActionHandler {
    private final SlotService slotService;
    private final WeightSensorService weightSensorService;
    private final SensorMetaDataService sensorMetaDataService;

    public BalanceNoSetHandler(SlotService slotService,
                               WeightSensorService weightSensorService,
                               SensorMetaDataService sensorMetaDataService) {
        this.slotService = slotService;
        this.weightSensorService = weightSensorService;
        this.sensorMetaDataService = sensorMetaDataService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Message onMessage(Message message) throws Exception {
        final Map<String, String> data = (Map<String, String>) MessageUtils.asObject(message.getData(), Map.class);
        if (data == null) {
            throw new BadRequestException("Empty Param Data!");
        }
        final Set<String> addressSet = data.keySet();
        for (String addressStr : addressSet) {
            val address = Integer.parseInt(addressStr);
            val slotNo = data.get(addressStr);
            final List<WeightSensor> sensors = weightSensorService.findByAddress(address);
            if (sensors.size() <= 0) {
                throw new BadRequestException("Invalidate Address!");
            }
            if (sensors.size() != 1) {
                final List<Integer> connectionIds = sensors
                        .stream()
                        .map(WeightSensor::getConnectionId)
                        .collect(Collectors.toList());
                log.error("Duplicate Address485 for Connections {}!", connectionIds);
                throw new InternalServerErrorException("Duplicate Address for connections [" + connectionIds + "]!");
            }
            final int slotId = sensors.get(0).getSlotId();
            slotService.updateSlotNo(slotId, slotNo);
        }
        sensorMetaDataService.refreshSlotTable();
        val res = Payload.ok();
        return MessageUtils.replyMessage(message, res);
    }
}
