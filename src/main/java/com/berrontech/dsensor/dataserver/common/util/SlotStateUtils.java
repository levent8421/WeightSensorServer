package com.berrontech.dsensor.dataserver.common.util;

import com.berrontech.dsensor.dataserver.common.entity.AbstractDevice485;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Create By Levent8421
 * Create Time: 2020/11/7 16:16
 * Class Name: SlotStateUtils
 * Author: Levent8421
 * Description:
 * 货道状态相关工具类
 *
 * @author Levent8421
 */
@Slf4j
public class SlotStateUtils {
    private static final Map<Integer, Integer> STATE_PRIORITY;

    static {
        STATE_PRIORITY = new HashMap<>(16);
        STATE_PRIORITY.put(AbstractDevice485.STATE_ONLINE, 1);
        STATE_PRIORITY.put(AbstractDevice485.STATE_OVERLOAD, 2);
        STATE_PRIORITY.put(AbstractDevice485.STATE_UNDER_LOAD, 3);
        STATE_PRIORITY.put(AbstractDevice485.STATE_DISABLE, 4);
        STATE_PRIORITY.put(AbstractDevice485.STATE_OFFLINE, 5);
        STATE_PRIORITY.put(AbstractDevice485.STATE_MERGED, 6);
    }

    /**
     * 获取合并货道的状态
     *
     * @param status 传感器状态列表
     * @return 状态
     */
    public static int getMergedSlotState(Collection<Integer> status) {
        int resState = -1;
        int maxPriovity = -1;
        for (int state : status) {
            final int priority = STATE_PRIORITY.get(state);
            if (priority > maxPriovity) {
                maxPriovity = priority;
                resState = state;
            }
        }
        return resState;
    }

    /**
     * 判断货道是否需要通知
     *
     * @param slot 货道
     * @return 是否需要通知
     */
    public static boolean needNotify(MemorySlot slot) {
        final String slotNo = slot.getSlotNo();
        if (slotNo == null) {
            return false;
        }
        return !slotNo.startsWith("#");
    }

    /**
     * 过滤需要通知的货道
     *
     * @param slots 货道
     * @return slots
     */
    public static List<MemorySlot> filterNotifySlots(Collection<MemorySlot> slots) {
        final List<MemorySlot> res = new ArrayList<>();
        for (MemorySlot slot : slots) {
            if (needNotify(slot)) {
                res.add(slot);
            } else {
                log.debug("Ignore notify slot: [{}]", slot.getSlotNo());
            }
        }
        return res;
    }
}
