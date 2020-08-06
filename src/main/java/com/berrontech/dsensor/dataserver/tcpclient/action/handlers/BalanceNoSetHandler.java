package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.common.exception.ResourceNotFoundException;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.weight.task.SensorMetaDataService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    private final SensorMetaDataService sensorMetaDataService;

    public BalanceNoSetHandler(SlotService slotService,
                               SensorMetaDataService sensorMetaDataService) {
        this.slotService = slotService;
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
        final Map<String, String> failureTable = new HashMap<>(16);
        boolean changed = false;
        for (String addressStr : addressSet) {
            val address = Integer.parseInt(addressStr);
            val slotNo = data.get(addressStr);
            try {
                doSetSlotNo(address, slotNo);
                changed = true;
            } catch (Exception e) {
                final String error = String.format("%s:%s", e.getClass().getSimpleName(), e.getMessage());
                log.debug("Error on set slotNo[{}] to address [{}],error=[{}]",
                        slotNo, address, error);
                failureTable.put(String.valueOf(address), error);
            }
        }
        if (changed) {
            sensorMetaDataService.refreshSlotTable();
        }
        val res = Payload.ok(failureTable);
        return MessageUtils.replyMessage(message, res);
    }

    /**
     * 执行设置货道号操作
     *
     * @param address address
     * @param slotNo  slotNo
     */
    private void doSetSlotNo(Integer address, String slotNo) {
        final Slot slot = slotService.findByAddress(address);
        if (slot == null) {
            final String error = String.format("物理地址[%s]不存在!", address);
            log.warn("Address Not Found [{}]!", error);
            throw new ResourceNotFoundException(error);
        }
        slotService.updateSlotNo(slot.getId(), slotNo);
    }
}
