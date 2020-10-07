package com.berrontech.dsensor.dataserver.service.general.impl;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.common.util.CollectionUtils;
import com.berrontech.dsensor.dataserver.common.util.TextUtils;
import com.berrontech.dsensor.dataserver.repository.mapper.SlotMapper;
import com.berrontech.dsensor.dataserver.repository.mapper.WeightSensorMapper;
import com.berrontech.dsensor.dataserver.service.basic.impl.AbstractServiceImpl;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
@Slf4j
public class SlotServiceImpl extends AbstractServiceImpl<Slot> implements SlotService {
    private final SlotMapper slotMapper;
    private final WeightSensorMapper weightSensorMapper;

    public SlotServiceImpl(SlotMapper slotMapper,
                           WeightSensorMapper weightSensorMapper) {
        super(slotMapper);
        this.slotMapper = slotMapper;
        this.weightSensorMapper = weightSensorMapper;
    }

    @Override
    public void updateSkuInfoBySlotNo(Slot slot) {
        final int rows = slotMapper.updateSkuInfoBySlotNo(slot);
        if (rows < 1) {
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

    @Override
    public List<Slot> createOrUpdateSlotsBySensor(Collection<WeightSensor> sensors, WeightSensorService weightSensorService) {
        val existsSlots = all();
        val existsSlotTable = existsSlots.stream().collect(Collectors.toMap(Slot::getId, v -> v));
        final Map<Integer, Slot> returnSlots = new HashMap<>(16);
        for (WeightSensor sensor : sensors) {
            final Slot slot;
            if (sensor.getSlotId() == null) {
                // 当前传感器未绑定货道 此时应该创建默认货道
                slot = createDefaultSlot(sensor);
                weightSensorService.updateById(sensor);
            } else {
                slot = existsSlotTable.get(sensor.getSlotId());
            }
            if (!returnSlots.containsKey(slot.getId())) {
                returnSlots.put(slot.getId(), slot);
            }
        }
        return new ArrayList<>(returnSlots.values());
    }

    @Override
    public void updateState(Integer id, int state) {
        final int rows = slotMapper.updateState(id, state);
        if (rows != 1) {
            throw new InternalServerErrorException("Error On Update SlotState, id=" + id + ",state=" + state);
        }
    }

    private Slot createDefaultSlot(WeightSensor sensor) {
        val slot = new Slot();
        slot.setAddress(sensor.getAddress());
        slot.setSlotNo(defaultSlotNo(sensor));
        slot.setHasElabel(sensor.getHasElabel());
        slot.setState(WeightSensor.STATE_ONLINE);
        save(slot);
        sensor.setSlotId(slot.getId());
        return slot;
    }

    private String defaultSlotNo(WeightSensor sensor) {
        return String.format("#-%d-%d", sensor.getConnectionId(), sensor.getAddress());
    }

    @Override
    public void setElabelState(Integer id, Boolean hasElabel) {
        final int rows = slotMapper.updateHasELable(id, hasElabel);
        if (rows != 1) {
            throw new InternalServerErrorException("Error On Update ELabel State! rows=" + rows);
        }
    }

    @Override
    public List<Slot> findBySku(String skuNo) {
        final Slot query = new Slot();
        query.setSkuNo(skuNo);
        return findByQuery(query);
    }

    @Override
    public Slot findByAddress(int address) {
        return slotMapper.selectByAddress(address);
    }

    @Override
    public void deleteByAddressList(List<Integer> addressList) {
        if (CollectionUtils.isEmpty(addressList)) {
            return;
        }
        final int rows = slotMapper.deleteByAddress(addressList);
        log.warn("Delete Slot[{}] By Address{}.", rows, addressList);
    }

    @Override
    public List<Slot> allWithSensors() {
        final List<WeightSensor> sensors = weightSensorMapper.selectAllWithSlot();
        final Map<Integer, List<WeightSensor>> sensorsMap = new HashMap<>(64);
        final Map<Integer, Slot> slotMap = new HashMap<>(64);
        final Slot noBindSlot = new Slot();
        noBindSlot.setId(-1);
        noBindSlot.setSlotNo("NoBind");
        for (WeightSensor sensor : sensors) {
            Slot slot = sensor.getSlot();
            if (slot == null) {
                slot = noBindSlot;
            }
            slotMap.put(slot.getId(), slot);
            final List<WeightSensor> slotSensors = sensorsMap.computeIfAbsent(slot.getId(), k -> new ArrayList<>());
            slotSensors.add(sensor);
            sensor.setSlot(null);
        }
        final List<Slot> slots = new ArrayList<>(slotMap.size());
        for (Slot slot : slotMap.values()) {
            slot.setSensors(sensorsMap.get(slot.getId()));
            slots.add(slot);
        }
        return slots;
    }

    @Override
    public List<Slot> findByIds(List<Integer> slotIds) {
        return slotMapper.selectByIds(CollectionUtils.join(slotIds.stream(), ","));
    }

    @Override
    public int mergeSlots(List<Slot> slots) {
        Slot minAddSlot = slots.get(0);
        for (Slot slot : slots) {
            if (slot.getAddress() < minAddSlot.getAddress()) {
                minAddSlot = slot;
            }
        }
        final List<Integer> slotsIds = slots.stream().map(Slot::getId).collect(Collectors.toList());
        return weightSensorMapper.updateSlotIdBySlotIds(slotsIds, minAddSlot.getId());
    }
}
