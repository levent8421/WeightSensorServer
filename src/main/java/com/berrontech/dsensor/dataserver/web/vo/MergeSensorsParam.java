package com.berrontech.dsensor.dataserver.web.vo;

import lombok.Data;

import java.util.Set;

/**
 * Create By Levent8421
 * Create Time: 2020/7/8 22:13
 * Class Name: MergeSensorsParam
 * Author: Levent8421
 * Description:
 * 合并货道参数
 *
 * @author Levent8421
 */
@Data
public class MergeSensorsParam {
    /**
     * 传感器ID列表
     */
    private Set<Integer> sensorIds;
    /**
     * 货道ID
     */
    private Integer slotId;
}
