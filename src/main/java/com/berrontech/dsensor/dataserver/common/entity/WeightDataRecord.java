package com.berrontech.dsensor.dataserver.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Create By Levent8421
 * Create Time: 2020/12/8 16:02
 * Class Name: WeightDataRecord
 * Author: Levent8421
 * Description:
 * 重力数据记录
 *
 * @author Levent8421
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_weight_data_record")
public class WeightDataRecord extends AbstractEntity {
    /**
     * 电子标签SN
     */
    @Column(name = "sensor_sn", length = 100)
    private String sensorSn;
    /**
     * 传感器地址
     */
    @Column(name = "sensor_address", length = 6, nullable = false)
    private Integer sensorAddress;
    /**
     * 传感器状态
     */
    @Column(name = "sensor_state", length = 3, nullable = false)
    private Integer sensorState;
    /**
     * 电子标签SN
     */
    @Column(name = "elabel_sn", length = 100)
    private String eLabelSn;
    /**
     * 电子标签状态
     */
    @Column(name = "elabel_state", length = 3, nullable = false)
    private Integer eLabelState;
    /**
     * 重量
     */
    @Column(name = "weight", length = 10, nullable = false)
    private BigDecimal weight;
    /**
     * 零点
     */
    @Column(name = "zero_offset", nullable = false)
    private Double zeroOffset;
    /**
     * 传感器丢包率
     */
    @Column(name = "sensor_error_rate", nullable = false)
    private Double sensorErrorRate;
    /**
     * 传感器丢包数量
     */
    @Column(name = "sensor_error_count", length = 10, nullable = false)
    private Long sensorErrorCount;
    /**
     * 电子标签丢包率
     */
    @Column(name = "elabel_error_rate", nullable = false)
    private Double eLabelErrorRate;
    /**
     * 电子标签丢包数
     */
    @Column(name = "elabel_error_count", length = 10, nullable = false)
    private Long eLabelErrorCount;
    /**
     * SKU单重
     */
    @Column(name = "sku_apw", length = 10)
    private BigDecimal skuApw;
    /**
     * 计数数量
     */
    @Column(name = "sku_pcs", length = 6, nullable = false)
    private Integer skuPcs;
}
