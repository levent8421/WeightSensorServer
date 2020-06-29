package com.berrontech.dsensor.dataserver.tcpclient.vo.data;

import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/6/19 12:58
 * Class Name: SkuParam
 * Author: Levent8421
 * Description:
 * 设置SKU参数
 *
 * @author Levent8421
 */
@Data
public class SkuParam {
    private String slotNo;
    private String name;
    private Integer apw;
    private String skuNo;
    private Integer tolerance;
    private Integer skuShelfLifeOpenDays;
}
