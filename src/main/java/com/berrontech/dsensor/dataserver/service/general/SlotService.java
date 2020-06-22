package com.berrontech.dsensor.dataserver.service.general;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.service.basic.AbstractService;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/6/17 12:15
 * Class Name: SlotService
 * Author: Levent8421
 * Description:
 * 货位相关业务行为定义
 *
 * @author Levent8421
 */
public interface SlotService extends AbstractService<Slot> {
    /**
     * 通过逻辑货道号更新SKU信息
     *
     * @param slot 包含SKU信息和SlotNo的Slot对象
     */
    void updateSkuInfoBySlotNo(Slot slot);

    /**
     * 更新逻辑货道号
     *
     * @param id     id
     * @param slotNo slotNO
     */
    void updateSlotNo(Integer id, String slotNo);

    /**
     * 通过SKU号或SKU名称查找数据
     *
     * @param skuNo   SKU号
     * @param skuName SKU名称
     * @return 货道列表
     */
    List<Slot> searchBySku(String skuNo, String skuName);
}
