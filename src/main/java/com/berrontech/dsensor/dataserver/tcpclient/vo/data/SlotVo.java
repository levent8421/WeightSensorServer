package com.berrontech.dsensor.dataserver.tcpclient.vo.data;

import com.berrontech.dsensor.dataserver.common.entity.AbstractDevice485;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySku;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightData;
import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/6/20 18:18
 * Class Name: SlotVo
 * Author: Levent8421
 * Description:
 * 货道ValueObject
 *
 * @author Levent8421
 */
@Data
public class SlotVo {
    public static SlotVo of(MemorySlot slot) {
        final SlotVo vo = new SlotVo();
        vo.setId(slot.getId());
        vo.setNo(slot.getSlotNo());
        vo.setHasElabel(slot.getHasElabel());
        vo.setState(AbstractDevice485.getStateString(slot.getState()));
        return vo;
    }

    public static SlotVo of(MemorySlot slot, MemorySku sku, MemoryWeightData data) {
        final SlotVo vo = of(slot);
        final SkuVo skuVo = SkuVo.of(sku);
        final WeightDataVo dataVo = WeightDataVo.of(data);
        vo.setSku(skuVo);
        vo.setData(dataVo);
        return vo;
    }


    private Integer id;
    private String no;
    private String state;
    private Boolean hasElabel;
    private String eLabelState;
    private SkuVo sku;

    private WeightDataVo data;
}
