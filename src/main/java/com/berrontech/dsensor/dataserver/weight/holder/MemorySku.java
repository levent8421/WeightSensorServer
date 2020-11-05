package com.berrontech.dsensor.dataserver.weight.holder;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Create By Levent8421
 * Create Time: 2020/6/18 20:07
 * Class Name: MemorySku
 * Author: Levent8421
 * Description:
 * SKU数据 内存保存
 *
 * @author Levent8421
 */
@Data
public class MemorySku implements Serializable {
    private String name;
    private String skuNo;
    private Integer apw;
    private Integer tolerance;
    private Integer shelfLifeOpenDays;
    private Date skuUpdateTime;
}
