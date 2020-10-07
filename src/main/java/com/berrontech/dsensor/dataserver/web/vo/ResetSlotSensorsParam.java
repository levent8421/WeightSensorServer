package com.berrontech.dsensor.dataserver.web.vo;

import lombok.Data;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/9/28 14:51
 * Class Name: ResetSlotSensorsParam
 * Author: Levent8421
 * Description:
 * 重置（拆分）货道传感器参数
 *
 * @author Levent8421
 */
@Data
public class ResetSlotSensorsParam {
    private List<Integer> slotIds;
}
