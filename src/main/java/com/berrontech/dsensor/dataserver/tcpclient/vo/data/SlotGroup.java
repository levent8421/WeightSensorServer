package com.berrontech.dsensor.dataserver.tcpclient.vo.data;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Create By Levent8421
 * Create Time: 2021/1/12 20:50
 * Class Name: SlotGroup
 * Author: Levent8421
 * Description:
 * Slot Group
 *
 * @author Levent8421
 */
@Data
public class SlotGroup {
    public static SlotGroup of(List<Slot> slots) {
        Slot primary = slots.get(0);
        for (Slot slot : slots) {
            if (slot.getAddress() < primary.getAddress()) {
                primary = slot;
            }
        }
        final List<SlotMergeNotifyVo> subsidiary = new ArrayList<>();
        for (Slot slot : slots) {
            if (Objects.equals(slot.getId(), primary.getId())) {
                continue;
            }
            subsidiary.add(SlotMergeNotifyVo.of(slot));
        }
        final SlotGroup slotGroup = new SlotGroup();
        slotGroup.setPrimary(SlotMergeNotifyVo.of(primary));
        slotGroup.setSubsidiary(subsidiary);
        return slotGroup;
    }

    private SlotMergeNotifyVo primary;
    private List<SlotMergeNotifyVo> subsidiary;
}
