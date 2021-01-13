package com.berrontech.dsensor.dataserver.tcpclient.vo.data;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2021/1/12 20:50
 * Class Name: SlotMergeNotifyVo
 * Author: Levent8421
 * Description:
 * Slot Merged notify vo
 *
 * @author Levent8421
 */
@Data
public class SlotMergeNotifyVo {
    public static SlotMergeNotifyVo of(Slot slot) {
        final SlotMergeNotifyVo vo = new SlotMergeNotifyVo();
        vo.setSlotNo(slot.getSlotNo());
        vo.setAddress(slot.getAddress());
        final SkuVo sku = new SkuVo();
        sku.setName(slot.getSkuName());
        sku.setSkuNo(slot.getSkuNo());
        sku.setApw(slot.getSkuApw());
        sku.setTolerance(slot.getSkuTolerance());
        sku.setShelfLifeOpenDays(slot.getSkuShelfLifeOpenDays());
        vo.setSku(sku);
        return vo;
    }

    private String slotNo;
    private Integer address;
    private SkuVo sku;
}
