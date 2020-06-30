package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Map;
import java.util.Objects;
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
    private final WeightController weightController;
    private final WeightDataHolder weightDataHolder;

    public BalanceNoSetHandler(SlotService slotService,
                               WeightController weightController,
                               WeightDataHolder weightDataHolder) {
        this.slotService = slotService;
        this.weightController = weightController;
        this.weightDataHolder = weightDataHolder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Message onMessage(Message message) throws Exception {
        final Map<String, String> data = (Map<String, String>) MessageUtils.asObject(message.getData(), Map.class);
        if (data == null) {
            throw new BadRequestException("Empty Param Data!");
        }
        final Set<String> idList = data.keySet();
        for (String idStr : idList) {
            val id = Integer.parseInt(idStr);
            val slotNo = data.get(idStr);
            slotService.updateSlotNo(id, slotNo);
            notifySlotNoChanged(id, slotNo);
        }
        val res = Payload.ok();
        return MessageUtils.replyMessage(message, res);
    }

    private void notifySlotNoChanged(int slotId, String slotNo) {
        val slotTable = weightDataHolder.getSlotTable();
        String oldSlotNo = null;
        for (val entry : slotTable.entrySet()) {
            val slot = entry.getValue();
            if (Objects.equals(slot.getId(), slotId)) {
                oldSlotNo = entry.getKey();
                break;
            }
        }
        if (oldSlotNo == null) {
            throw new InternalServerErrorException("Could Not Found Slot[" + slotId + "]");
        }
        val slot = slotTable.remove(oldSlotNo);
        slotTable.put(slotNo, slot);
        weightController.updateSlotNo(slotId, slotNo);
    }
}
