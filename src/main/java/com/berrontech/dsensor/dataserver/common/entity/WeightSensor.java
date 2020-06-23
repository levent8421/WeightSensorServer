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
     * 货位ID
     */
    @Column(name = "slot_id", length = 10)
    private Integer slotId;
    /**
     * 管理按的货位对象
     */
    private Slot slot;
}
