package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.SkuParam;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySku;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.val;

import java.util.List;

import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notEmpty;
import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notNull;

/**
 * Create By Levent8421
 * Create Time: 2020/6/19 11:42
 * Class Name: BalanceSkuSetHandler
 * Author: Levent8421
 * Description:
 * 设置货道SKU
 *
 * @author Levent8421
 */
@ActionHandlerMapping("balance.sku.set")
public class BalanceSkuSetHandler implements ActionHandler {
    private final SlotService slotService;
    private final WeightController weightController;
    private final WeightDataHolder weightDataHolder;

    public BalanceSkuSetHandler(SlotService slotService, WeightController weightController, WeightDataHolder weightDataHolder) {
        this.slotService = slotService;
        this.weightController = weightController;
        this.weightDataHolder = weightDataHolder;
    }

    @Override
    public Message onMessage(Message message) throws Exception {
        final Object dataObj = message.getData();
        final List<SkuParam> params = MessageUtils.asList(dataObj, SkuParam.class);
        checkParams(params);
        for (SkuParam param : params) {
            final Slot query = new Slot();
            query.setSlotNo(param.getSlotNo());
            query.setSkuNo(param.getSkuNo());
            query.setSkuName(param.getName());
            query.setSkuApw(param.getApw());
            query.setSkuTolerance(param.getTolerance());
            slotService.updateSkuInfoBySlotNo(query);
            notifySkuChanged(param.getSlotNo(), param);
        }
        val res = Payload.ok();
        return MessageUtils.replyMessage(message, res);
    }

    private void notifySkuChanged(String slotNo, SkuParam param) {
        final MemorySku sku = new MemorySku();
        sku.setSkuNo(param.getSkuNo());
        sku.setName(param.getName());
        sku.setApw(param.getApw());
        sku.setTolerance(param.getTolerance());
        weightController.setSku(slotNo, sku);

        final MemorySlot slot = weightDataHolder.getSlotTable().get(slotNo);
        slot.setSku(sku);
    }

    private void checkParams(List<SkuParam> params) {
        if (params == null) {
            throw new BadRequestException("Empty Params!");
        }
        val e = BadRequestException.class;
        for (SkuParam param : params) {
            if (param == null) {
                throw new BadRequestException("Empty(null) Param!");
            }
            val slotNo = param.getSlotNo();
            notEmpty(slotNo, e, "Empty SlotNo!");
            notEmpty(param.getName(), e, slotNo + "Empty SkuName!");
            notNull(param.getApw(), e, slotNo + "Empty SkuAPW!");
            notEmpty(param.getSkuNo(), e, slotNo + "Empty SkuNo!");
            notNull(param.getTolerance(), e, slotNo + "Empty Tolerance!");
        }
    }
}
