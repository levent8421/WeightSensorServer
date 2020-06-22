package com.berrontech.dsensor.dataserver.service.general.impl;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.common.util.TextUtils;
import com.berrontech.dsensor.dataserver.repository.mapper.SlotMapper;
import com.berrontech.dsensor.dataserver.service.basic.impl.AbstractServiceImpl;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/6/17 12:15
 * Class Name: SlotServiceImpl
 * Author: Levent8421
 * Description:
 * 货位相关业务行为实现
 *
 * @author Levent8421
 */
@Service
public class SlotServiceImpl extends AbstractServiceImpl<Slot> implements SlotService {
    private final SlotMapper slotMapper;

    public SlotServiceImpl(SlotMapper slotMapper) {
        super(slotMapper);
        this.slotMapper = slotMapper;
    }

    @Override
    public void updateSkuInfoBySlotNo(Slot slot) {
        final int rows = slotMapper.updateSkuInfoBySlotNo(slot);
        if (rows != 1) {
            throw new InternalServerErrorException(
                    "Error On Update SkuInfo for Slot["
                            + slot.getSlotNo()
                            + "],res rows="
                            + rows);
        }
    }

    @Override
    public void updateSlotNo(Integer id, String slotNo) {
        final int rows = slotMapper.updateSlotNoById(id, slotNo);
        if (rows != 1) {
            throw new InternalServerErrorException("Error On Update SlotNo! id=" + id + ",slotNo=" + slotNo + ",rows=" + rows);
        }
    }

    @Override
    public List<Slot> searchBySku(String skuNo, String skuName) {
        if (TextUtils.isTrimedEmpty(skuNo)) {
            skuNo = "%" + skuNo + "%";
        }
        if (TextUtils.isTrimedEmpty(skuName)) {
            skuName = "%" + skuName + "%";
        }
        return slotMapper.selectBySkuLike(skuNo, skuName);
    }
}
