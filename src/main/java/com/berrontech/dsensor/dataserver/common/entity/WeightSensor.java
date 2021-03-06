package com.berrontech.dsensor.dataserver.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * Create By Levent8421
 * Create Time: 2020/6/15 13:41
 * Class Name: WeightSensor
 * Author: Levent8421
 * Description:
 * 称重传感器
 *
 * @author Levent8421
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_weight_sensor")
public class WeightSensor extends AbstractDevice485 {
    /**
     * 类型：默认
     */
    public static final int TYPE_DEFAULT = 0x00;
    /**
     * 类型：普通货道
     */
    public static final int TYPE_NORMAL = 0x01;
    /**
     * 类型：灵活货道
     */
    public static final int TYPE_MERGEABLE = 0x02;
    /**
     * 类型：地堆货道
     */
    public static final int TYPE_LARGE = 0x03;
    /**
     * 类型：冰箱货道
     */
    public static final int TYPE_FRIDGE = 0x04;
    /**
     * 类型：吊篮货道
     */
    public static final int TYPE_BASKETS = 0x05;
    /**
     * 默认零点参考
     */
    public static final double DEFAULT_ZERO_REFERENCE = 0D;
    /**
     * 零点参考
     */
    @Column(name = "zero_reference", nullable = false)
    private Double zeroReference;
    /**
     * 配置字符串
     */
    @Column(name = "config_str")
    private String configStr;
    /**
     * 货位ID
     */
    @Column(name = "slot_id", length = 10)
    private Integer slotId;
    /**
     * 管理按的货位对象
     */
    private Slot slot;
    /**
     * 是否有电子标签
     */
    @Column(name = "has_elabel", nullable = false, length = 1)
    private Boolean hasElabel;
    /**
     * 传感器类型
     */
    @Column(name = "type", length = 3, nullable = false)
    private Integer type;
    /**
     * 传感器SN
     */
    @Column(name = "sensor_sn", length = 10)
    private String sensorSn;
    /**
     * 电子标签SN
     */
    @Column(name = "elabel_sn", length = 100)
    private String elabelSn;
}
