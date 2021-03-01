package com.berrontech.dsensor.dataserver.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
     * 货道绑定原地址
     */
    @Column(name = "address", length = 10, nullable = false)
    private Integer address;
    /**
     * 货位号
     */
    @Column(name = "slot_no", nullable = false)
    private String slotNo;
    /**
     * 皮重
     */
    @Column(name = "tare_value", length = 10, nullable = false)
    private BigDecimal tareValue;
    /**
     * SKU 号
     */
    @Column(name = "sku_no")
    private String skuNo;
    /**
     * SKU 名称
     */
    @Column(name = "sku_name")
    private String skuName;
    /**
     * SKU 单重
     */
    @Column(name = "sku_apw", length = 10)
    private BigDecimal skuApw;
    /**
     * SKU 允差
     */
    @Column(name = "sku_tolerance", length = 10)
    private BigDecimal skuTolerance;
    /**
     * 开封后保存天数
     */
    @Column(name = "sku_shelf_life_open_days")
    private Integer skuShelfLifeOpenDays;
    /**
     * SKU 更新时间
     */
    @Column(name = "sku_update_time")
    private Date skuUpdateTime;
    /**
     * 是否有电子标签
     */
    @Column(name = "has_elabel")
    private Boolean hasElabel;
    /**
     * 状态
     */
    @Column(name = "state")
    private Integer state;
    /**
     * 是否不可拆分，true:不可拆分 false:可拆分
     */
    @Column(name = "indivisible", nullable = false, length = 1)
    private Boolean indivisible;
    /**
     * 传感器列表
     */
    private List<WeightSensor> sensors;
}
