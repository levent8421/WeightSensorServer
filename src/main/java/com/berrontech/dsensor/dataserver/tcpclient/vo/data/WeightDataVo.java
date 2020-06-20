package com.berrontech.dsensor.dataserver.tcpclient.vo.data;

import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightData;
import lombok.Data;
import lombok.val;

/**
 * Create By Levent8421
 * Create Time: 2020/6/20 18:23
 * Class Name: WeightDataVo
 * Author: Levent8421
 * Description:
 * Weight Data Value Object
 *
 * @author Levent8421
 */
@Data
public class WeightDataVo {
    public static String asWeightStateString(Integer state) {
        if (state == null) {
            return null;
        }
        switch (state) {
            case MemoryWeightData.WEIGHT_STATE_DYNAMIC:
                return "dynamic";
            case MemoryWeightData.WEIGHT_STATE_STABLE:
                return "stable";
            default:
                return String.valueOf(state);
        }
    }

    public static String asToleranceStateString(Integer state) {
        if (state == null) {
            return null;
        }
        switch (state) {
            case MemoryWeightData.TOLERANCE_STATE_CREDIBLE:
                return "credible";
            case MemoryWeightData.TOLERANCE_STATE_INCREDIBLE:
                return "incredible";
            default:
                return String.valueOf(state);
        }
    }

    public static WeightDataVo of(MemoryWeightData data) {
        if (data == null) {
            return null;
        }
        val vo = new WeightDataVo();
        vo.setWeight(data.getWeight());
        vo.setWeightState(asWeightStateString(data.getWeightState()));
        vo.setCount(data.getCount());
        vo.setTolerance(data.getTolerance());
        vo.setToleranceState(asToleranceStateString(data.getToleranceState()));
        return vo;
    }

    private Integer weight;
    private String weightState;
    private Integer count;
    private Integer tolerance;
    private String toleranceState;
}
