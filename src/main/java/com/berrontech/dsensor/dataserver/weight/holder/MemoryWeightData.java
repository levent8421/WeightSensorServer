package com.berrontech.dsensor.dataserver.weight.holder;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Create By Levent8421
 * Create Time: 2020/6/18 20:13
 * Class Name: MemoryWeightData
 * Author: Levent8421
 * Description:
 * 称重数据 内存保持
 *
 * @author Levent8421
 */
@Data
public class MemoryWeightData implements Serializable {
    /**
     * 称重状态 稳定
     */
    public static final int WEIGHT_STATE_STABLE = 0x01;
    /**
     * 称重状态 不稳定
     */
    public static final int WEIGHT_STATE_DYNAMIC = 0x02;
    /**
     * 称重状态 欠载
     */
    public static final int WEIGHT_STATE_UNDERLOAD = 0x03;
    /**
     * 称重状态 过载
     */
    public static final int WEIGHT_STATE_OVERLOAD = 0x04;
    /**
     * 误差状态 可信
     */
    public static final int TOLERANCE_STATE_CREDIBLE = 0x01;
    /**
     * 误差状态 不可信
     */
    public static final int TOLERANCE_STATE_INCREDIBLE = 0x02;
    private BigDecimal weight;
    private Integer weightState;
    private Integer count;
    private BigDecimal tolerance;
    private Integer toleranceState;
}
