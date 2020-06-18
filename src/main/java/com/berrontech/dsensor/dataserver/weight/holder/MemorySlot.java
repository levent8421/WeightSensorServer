package com.berrontech.dsensor.dataserver.weight.holder;

import java.util.Collection;

/**
 * Create By Levent8421
 * Create Time: 2020/6/18 20:18
 * Class Name: MemorySlot
 * Author: Levent8421
 * Description:
 * 货道 内存保持
 *
 * @author Levent8421
 */
public class MemorySlot {
    private String slotNo;
    private Collection<MemoryWeightSensor> sensors;
    private MemorySku sku;
    private MemoryWeightData data;
}
