package com.berrontech.dsensor.dataserver.common.util;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create By Levent8421
 * Create Time: 2021/1/13 16:00
 * Class Name: SlotGroupUtils
 * Author: Levent8421
 * Description:
 * 货道组工具类
 *
 * @author Levent8421
 */
@Slf4j
public class SlotGroupUtils {
    /**
     * 将传感器列表转换为 slot{sensors:[xxx,xxx]}的格式
     * 注意：该方法要求传入的传感器必须包含货道信息 即<code>sensor.slot!=null</code>
     *
     * @param sensorsWithSlot sensors
     * @return slots
     */
    public static List<Slot> asSlotSensorsObjects(List<WeightSensor> sensorsWithSlot) {
        final Map<Integer, Slot> slotTable = new HashMap<>(16);
        for (WeightSensor sensor : sensorsWithSlot) {
            Slot slot = sensor.getSlot();
            if (slot == null) {
                log.warn("WeightSensor [addr:{},id:{}] no slot Binding!", sensor.getAddress(), sensor.getId());
                continue;
            }
            if (slotTable.containsKey(slot.getId())) {
                slot = slotTable.get(slot.getId());
            } else {
                slot.setSensors(new ArrayList<>());
                slotTable.put(slot.getId(), slot);
            }
            sensor.setSlot(null);
            slot.getSensors().add(sensor);
        }
        return new ArrayList<>(slotTable.values());
    }

    /**
     * 将货道列表转换为货道分组信息
     * 注意：该方法要求传入的货道信息包含传感器列表 即<code>slot.sensors!=null</code>
     *
     * @param slots       slots
     * @param slotService slotService
     * @return slot groups
     */
    public static List<List<Slot>> asSlotGroups(List<Slot> slots, SlotService slotService) {
        final List<Integer> fetchAddressList = new ArrayList<>();
        for (Slot slot : slots) {
            for (WeightSensor s : slot.getSensors()) {
                fetchAddressList.add(s.getAddress());
            }
        }
        final List<Slot> allSlots = slotService.findByAddressList(fetchAddressList);
        final Map<Integer, Slot> addrSlotTable = allSlots.stream().collect(Collectors.toMap(Slot::getAddress, s -> s));
        final List<List<Slot>> res = new ArrayList<>();
        for (Slot slot : slots) {
            final List<Slot> slotGroup = new ArrayList<>();
            res.add(slotGroup);
            for (WeightSensor sensor : slot.getSensors()) {
                final Slot s = addrSlotTable.get(sensor.getAddress());
                if (s == null) {
                    throw new InternalServerErrorException("Can not find slot by address:" + sensor.getAddress());
                }
                slotGroup.add(s);
            }
        }
        return res;
    }
}
