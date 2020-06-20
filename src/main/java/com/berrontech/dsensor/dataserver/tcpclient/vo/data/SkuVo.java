package com.berrontech.dsensor.dataserver.tcpclient.vo.data;

import com.berrontech.dsensor.dataserver.weight.holder.MemorySku;
import lombok.Data;
import lombok.val;

/**
 * Create By Levent8421
 * Create Time: 2020/6/20 18:22
 * Class Name: SkuVo
 * Author: Levent8421
 * Description:
 * SKU Value Object
 *
 * @author Levent8421
 */
@Data
public class SkuVo {
    public static SkuVo of(MemorySku sku) {
        if (sku == null) {
            return null;
        }
        val vo = new SkuVo();
        vo.setName(sku.getName());
        vo.setSkuNo(sku.getSkuNo());
        vo.setApw(sku.getApw());
        vo.setTolerance(sku.getTolerance());
        return vo;
    }

    private String name;
    private String skuNo;
    private Integer apw;

    private Integer tolerance;
}
