package com.berrontech.dsensor.dataserver.web.vo;

import lombok.Data;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/9/28 14:25
 * Class Name: SlotMergeParam
 * Author: Levent8421
 * Description:
 * 传感器合并参数
 *
 * @author Levent8421
 */
@Data
public class SlotMergeParam {
    private List<Integer> slotIds;
}
