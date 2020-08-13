package com.berrontech.dsensor.dataserver.tcpclient.notify.impl;

import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create By Levent8421
 * Create Time: 2020/8/13 19:07
 * Class Name: WeightChangedEventBuffer
 * Author: Levent8421
 * Description:
 * Weight Changed Notify Event Buffer
 *
 * @author Levent8421
 */
public class WeightChangedEventBuffer {
    private final Map<String, MemorySlot> bufferTable = new ConcurrentHashMap<>(128);

    private void pushEvent(MemorySlot event) {
        final String slotNo = event.getSlotNo();
        bufferTable.put(slotNo, event);
    }

    void pushEvent(Collection<MemorySlot> events) {
        for (MemorySlot event : events) {
            pushEvent(event);
        }
    }

    public List<MemorySlot> copyEventAndClean() {
        if (bufferTable.size() <= 0) {
            return Collections.emptyList();
        }
        synchronized (bufferTable) {
            final List<MemorySlot> messages = new ArrayList<>(bufferTable.values());
            bufferTable.clear();
            return messages;
        }
    }
}
