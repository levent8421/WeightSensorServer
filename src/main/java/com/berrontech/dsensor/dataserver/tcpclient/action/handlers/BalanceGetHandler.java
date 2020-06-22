package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.val;

/**
 * Create By Levent8421
 * Create Time: 2020/6/22 18:02
 * Class Name: BalanceGetHandler
 * Author: Levent8421
 * Description:
 * 获取全部货道
 *
 * @author Levent8421
 */
@ActionHandlerMapping("balance.list")
public class BalanceGetHandler implements ActionHandler {
    private final WeightDataHolder weightDataHolder;

    public BalanceGetHandler(WeightDataHolder weightDataHolder) {
        this.weightDataHolder = weightDataHolder;
    }

    @Override
    public Message onMessage(Message message) {
        val list = weightDataHolder.getSlotTable().values();
        val data = Payload.ok(list);
        return MessageUtils.replyMessage(message, data);
    }
}
