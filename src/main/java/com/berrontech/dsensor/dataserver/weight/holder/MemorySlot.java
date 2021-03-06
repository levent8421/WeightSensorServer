package com.berrontech.dsensor.dataserver.weight.holder;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import lombok.Data;

import java.io.Serializable;
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
@Data
public class MemorySlot implements Serializable {
    public static MemorySlot of(Slot slot) {
        final MemorySlot ms = new MemorySlot();
        ms.setSlotNo(slot.getSlotNo());
        ms.setId(slot.getId());
        ms.setHasElabel(slot.getHasElabel());
        ms.setState(slot.getState());

        final MemorySku sku = new MemorySku();
        sku.setName(slot.getSkuName());
        sku.setSkuNo(slot.getSkuNo());
        sku.setApw(slot.getSkuApw());
        sku.setTolerance(slot.getSkuTolerance());
        sku.setShelfLifeOpenDays(slot.getSkuShelfLifeOpenDays());
        sku.setSkuUpdateTime(slot.getSkuUpdateTime());
        ms.setSku(sku);
        return ms;
    }

    private Integer id;
    private String slotNo;
    private Integer state;
    private Boolean hasElabel;
    private Collection<MemoryWeightSensor> sensors;
    private MemorySku sku;
    private MemoryWeightData data;
}
