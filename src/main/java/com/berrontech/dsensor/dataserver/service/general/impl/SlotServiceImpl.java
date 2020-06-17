package com.berrontech.dsensor.dataserver.service.general.impl;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.repository.mapper.SlotMapper;
import com.berrontech.dsensor.dataserver.service.basic.impl.AbstractServiceImpl;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import org.springframework.stereotype.Service;

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
}
