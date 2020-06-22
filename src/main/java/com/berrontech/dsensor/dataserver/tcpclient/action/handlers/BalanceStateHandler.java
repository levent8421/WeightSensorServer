package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.common.entity.AbstractDevice485;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.common.util.ParamChecker;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.BalanceStateParams;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.val;

/**
 * Create By Levent8421
 * Create Time: 2020/6/22 18:05
 * Class Name: BalanceStateHandler
 * Author: Levent8421
 * Description:
 * 设置货道状态
 *
 * @author Levent8421
 */
@ActionHandlerMapping("balance.state")
public class BalanceStateHandler implements ActionHandler {

    private final SlotService slotService;
    private final WeightDataHolder weightDataHolder;
    private final WeightController weightController;

    public BalanceStateHandler(SlotService slotService,
                               WeightDataHolder weightDataHolder,
                               WeightController weightController) {
        this.slotService = slotService;
        this.weightDataHolder = weightDataHolder;
        this.weightController = weightController;
    }


    @Override
    public Message onMessage(Message message) throws Exception {
        final BalanceStateParams params = MessageUtils.asObject(message.getData(), BalanceStateParams.class);
        checkParams(params);
        final int state = getState(params.getState());
        notifySlotStateChange(params.getSlotNo(), state);
        val data = Payload.ok();
        return MessageUtils.replyMessage(message, data);
    }

    private void notifySlotStateChange(String slotNo, int state) {
        val slot = weightDataHolder.getSlotTable().get(slotNo);
        slot.setState(state);
        final int slotId = slot.getId();
        slotService.updateState(slotId, state);
        weightController.onSlotStateChanged(slotNo, state);
    }

    private void checkParams(BalanceStateParams params) {
        ParamChecker.notNull(params, BadRequestException.class, "No Params!");
        ParamChecker.notEmpty(params.getSlotNo(), BadRequestException.class, "Empty SlotNo!");
        ParamChecker.notEmpty(params.getState(), BadRequestException.class, "Empty State!");
    }

    private int getState(String state) {
        switch (state) {
            case "enable":
                return AbstractDevice485.STATE_ONLINE;
            case "disable":
                return AbstractDevice485.STATE_DISABLE;
            default:
                throw new BadRequestException("Invalidate State[" + state + "]");
        }
    }
}
