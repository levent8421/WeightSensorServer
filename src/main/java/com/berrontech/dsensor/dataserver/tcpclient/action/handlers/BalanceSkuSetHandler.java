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
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Slf4j
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
        notEmpty(params, BadRequestException.class, "Empty param list!");
        assert params != null;

        final Map<String, String> failureSlotNos = new HashMap<>(16);
        for (SkuParam param : params) {
            if (param == null) {
                log.warn("Receive a null param(On set sku api[{}])!", message.getSeqNo());
                continue;
            }
            try {
                doSetSku(param);
            } catch (Exception e) {
                failureSlotNos.put(param.getSlotNo(), String.format("%s:%s", e.getClass().getSimpleName(), e.getMessage()));
                log.warn("API message[{}], Error on set slot [{}] sku to [{}],error=[{},{}]",
                        message.getSeqNo(), param.getSlotNo(), param, e.getClass().getName(), e.getMessage(), e);
            }
        }
        final Payload<Map<String, String>> payload = Payload.ok(failureSlotNos);
        return MessageUtils.replyMessage(message, payload);
    }

    private void doSetSku(SkuParam param) {
        checkParam(param);
        // ------DATABASE OPERATION START------
        final Slot queryParam = new Slot();
        queryParam.setSlotNo(param.getSlotNo());
        queryParam.setSkuName(param.getName());
        queryParam.setSkuNo(param.getSkuNo());
        queryParam.setSkuApw(param.getApw());
        queryParam.setSkuTolerance(param.getTolerance());
        queryParam.setSkuShelfLifeOpenDays(param.getSkuShelfLifeOpenDays());
        slotService.updateSkuInfoBySlotNo(queryParam);
        // ------DATABASE OPERATION END------
        // NOTIFY WEIGHT SERVICE SKU CHANGED
        notifySkuChanged(param.getSlotNo(), param);
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

    private void checkParam(SkuParam param) {
        final Class<BadRequestException> ex = BadRequestException.class;
        notNull(param, ex, "参数不能为空!");
        notEmpty(param.getSlotNo(), ex, "货道号slotNo不能为空!");
        notEmpty(param.getName(), ex, "SKU名称不能为空!");
        notNull(param.getApw(), ex, "SKU单重不能为空!");
        notNull(param.getTolerance(), ex, "SKU允差不能为空!");
        notEmpty(param.getSkuNo(), ex, "SKU号不能为空!");
    }
}
