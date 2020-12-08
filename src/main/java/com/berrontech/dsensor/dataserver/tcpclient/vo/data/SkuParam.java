package com.berrontech.dsensor.dataserver.tcpclient.vo.data;

import lombok.Data;

import java.math.BigDecimal;

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
    private BigDecimal apw;
    private String skuNo;
    private BigDecimal tolerance;
    private Integer skuShelfLifeOpenDays;
}
