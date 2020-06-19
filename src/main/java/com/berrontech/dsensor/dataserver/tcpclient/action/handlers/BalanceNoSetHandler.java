package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

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

    public BalanceNoSetHandler(SlotService slotService) {
        this.slotService = slotService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Message onMessage(Message message) throws Exception {
        final Map<Integer, String> data = (Map<Integer, String>) MessageUtils.asObject(message.getData(), Map.class);
        if (data == null) {
            throw new BadRequestException("Empty Param Data!");
        }
        final Set<Integer> idList = data.keySet();
        for (Integer id : idList) {
            val slotNo = data.get(id);
            slotService.updateSlotNo(id, slotNo);
        }
        val res = Payload.ok();
        return MessageUtils.replyMessage(message, res);
    }
}
