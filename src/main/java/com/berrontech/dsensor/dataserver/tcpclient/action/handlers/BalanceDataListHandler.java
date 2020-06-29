package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.SlotVo;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.val;

import java.util.stream.Collectors;

/**
 * Create By Levent8421
 * Create Time: 2020/6/29 12:02
 * Class Name: BalanceDataListHandler
 * Author: Levent8421
 * Description:
 * 获取全部重量数据
 *
 * @author Levent8421
 */
@ActionHandlerMapping("balance.data.list")
public class BalanceDataListHandler implements ActionHandler {
    private final WeightDataHolder weightDataHolder;

    public BalanceDataListHandler(WeightDataHolder weightDataHolder) {
        this.weightDataHolder = weightDataHolder;
    }

    @Override
    public Message onMessage(Message message) throws Exception {
        val data = weightDataHolder.getSlotTable().values().stream().map(this::asSlotVo).collect(Collectors.toList());
        val payload = Payload.ok(data);
        return MessageUtils.replyMessage(message, payload);
    }

    private SlotVo asSlotVo(MemorySlot slot) {
        if (slot == null) {
            return null;
        }
        return SlotVo.of(slot, slot.getSku(), slot.getData());
    }
}
