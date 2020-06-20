package com.berrontech.dsensor.dataserver.tcpclient.vo.data;

import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
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
