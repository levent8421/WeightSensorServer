package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.util.SlotGroupUtils;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.SlotGroup;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Create By Levent8421
 * Create Time: 2021/1/13 16:46
 * Class Name: BalanceGroupsGetHandler
 * Author: Levent8421
 * Description:
 * 处理查询货道分组请求
 *
 * @author Levent8421
 */
@ActionHandlerMapping("balance.groups.get")
public class BalanceGroupsGetHandler implements ActionHandler {
    private final WeightSensorService weightSensorService;
    private final SlotService slotService;

    public BalanceGroupsGetHandler(WeightSensorService weightSensorService, SlotService slotService) {
        this.weightSensorService = weightSensorService;
        this.slotService = slotService;
    }

    @Override
    public Message onMessage(Message message) {
        final List<WeightSensor> sensors = weightSensorService.dumpAll();
        final List<Slot> slots = SlotGroupUtils.asSlotSensorsObjects(sensors);
        final List<List<Slot>> groups = SlotGroupUtils.asSlotGroups(slots, slotService);
        final List<SlotGroup> data = groups.stream().map(SlotGroup::of).collect(Collectors.toList());
        return MessageUtils.replyMessage(message, Payload.ok(data));
    }
}
