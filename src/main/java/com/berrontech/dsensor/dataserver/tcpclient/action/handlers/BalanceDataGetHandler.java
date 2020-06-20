package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.common.util.ParamChecker;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.SkuVo;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.SlotVo;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.WeightDataVo;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create By Levent8421
 * Create Time: 2020/6/20 18:07
 * Class Name: BalanceDataGetHandler
 * Author: Levent8421
 * Description:
 * 称重数据获取
 *
 * @author Levent8421
 */
@Slf4j
@ActionHandlerMapping("balance.data.get")
public class BalanceDataGetHandler implements ActionHandler {
    private final WeightDataHolder weightDataHolder;

    public BalanceDataGetHandler(WeightDataHolder weightDataHolder) {
        this.weightDataHolder = weightDataHolder;
    }

    @Override
    public Message onMessage(Message message) throws Exception {
        final List<String> slotNoList = MessageUtils.asList(message.getData(), String.class);
        ParamChecker.notEmpty(slotNoList, BadRequestException.class, "Empty SlotNo List!");
        assert slotNoList != null;
        val slotList = findSlotList(slotNoList);
        final List<SlotVo> slotResList = slotList.stream().map(this::asSlotVo).collect(Collectors.toList());
        final Payload<List<SlotVo>> res = Payload.ok(slotResList);
        return MessageUtils.replyMessage(message, res);
    }

    private List<MemorySlot> findSlotList(List<String> slotNoList) {
        val slotList = new ArrayList<MemorySlot>();
        val slotTable = weightDataHolder.getSlotTable();
        for (String slotNo : slotNoList) {
            if (!slotTable.containsKey(slotNo)) {
                throw new BadRequestException("Invalidate SlotNo[" + slotNo + "]!");
            }
            slotList.add(slotTable.get(slotNo));
        }
        return slotList;
    }

    private SlotVo asSlotVo(MemorySlot slot) {
        if (slot == null) {
            return null;
        }
        val slotVo = SlotVo.of(slot);
        val sku = SkuVo.of(slot.getSku());
        slotVo.setSku(sku);
        val data = WeightDataVo.of(slot.getData());
        slotVo.setData(data);
        return slotVo;
    }
}
