package com.berrontech.dsensor.dataserver.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Table;

/**
 * Create By Levent8421
 * Create Time: 2020/6/15 13:49
 * Class Name: Slot
 * Author: Levent8421
 * Description:
 * Slot 货位
 *
 * @author Levent8421
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_slot")
public class Slot extends AbstractEntity {
    /**
     * 组合485地址
     */
    private Integer address;
    /**
     * 货位号
     */
    private String slotNo;
    /**
     * SKU 号
     */
    private String skuNo;
    /**
     * SKU 名称
     */
    private String skuName;
    /**
     * SKU 单重
     */
    private Integer skuApw;
    /**
     * SKU 允差
     */
    private Integer skuTolerance;
    /**
     * 是否有电子标签
     */
    private Boolean hasElabel;
}
